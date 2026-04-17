package tourist_cocoon.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tourist_cocoon.dto.ActualizarEstadoIncidenciaRequestDTO;
import tourist_cocoon.dto.CrearIncidenciaRequestDTO;
import tourist_cocoon.dto.IncidenciaResponseDTO;
import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.Incidencia;
import tourist_cocoon.model.OrdenLimpieza;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.model.enums.CategoriaIncidencia;
import tourist_cocoon.model.enums.EstadoCapsula;
import tourist_cocoon.model.enums.EstadoIncidencia;
import tourist_cocoon.model.enums.EstadoOrdenLimpieza;
import tourist_cocoon.model.enums.EstadoReserva;
import tourist_cocoon.model.enums.PrioridadIncidencia;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.IncidenciaRepository;
import tourist_cocoon.repository.OrdenLimpiezaRepository;
import tourist_cocoon.repository.ReservaRepository;
import tourist_cocoon.repository.UsuarioRepository;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private CapsulaRepository capsulaRepository;

    @Autowired
    private OrdenLimpiezaRepository ordenLimpiezaRepository;

    @Autowired
    private ReservaService reservaService;

    @Value("${app.incidencias.telefono-emergencia:+34 600 000 000}")
    private String telefonoEmergencia;

    @Transactional
    public IncidenciaResponseDTO reportar(CrearIncidenciaRequestDTO dto) {
        Usuario huesped = usuarioRepository.findById(dto.getHuespedId())
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));

        Reserva reservaActiva = reservaService.reservaActiva(dto.getHuespedId());
        Reserva reserva = resolverReservaContexto(dto, huesped, reservaActiva);

        if (dto.getCategoria() != CategoriaIncidencia.ACCESO && reserva == null) {
            throw new IllegalArgumentException("Se requiere una reserva activa para reportar esta incidencia.");
        }

        Capsula capsula = resolverCapsulaContexto(dto, reserva);

        Incidencia incidencia = new Incidencia();
        incidencia.setHuesped(huesped);
        incidencia.setReserva(reserva);
        incidencia.setCapsula(capsula);
        incidencia.setCategoria(dto.getCategoria());
        incidencia.setEstado(EstadoIncidencia.ABIERTA);
        incidencia.setDescripcion(dto.getDescripcion().trim());
        incidencia.setFotoPath(dto.getFotoUrl());
        incidencia.setPrioridad(calcularPrioridadAutomatica(dto.getCategoria(), dto.getDescripcion()));
        incidencia.setCanalNotificacion(normalizarCanal(dto.getCanalNotificacion()));

        EfectosIncidencia efectos = ejecutarEfectosSecundarios(incidencia);

        boolean notificacionEnviada;
        boolean telefonoEnRespuesta = Boolean.TRUE.equals(efectos.telefonoEmergenciaMostrado);
        try {
            notificacionEnviada = intentarNotificarPersonal(incidencia, dto.getCategoria() == CategoriaIncidencia.SEGURIDAD);
        } catch (Exception ex) {
            notificacionEnviada = false;
            telefonoEnRespuesta = true;
        }

        if (dto.getCategoria() == CategoriaIncidencia.ACCESO && !Boolean.TRUE.equals(efectos.resueltaAutomaticamente)) {
            telefonoEnRespuesta = true;
        }

        incidencia.setNotificacionEnviada(notificacionEnviada);
        incidencia.setTelefonoEmergenciaMostrado(telefonoEnRespuesta);

        Incidencia guardada = incidenciaRepository.save(incidencia);
        return toResponse(guardada, efectos);
    }

    @Transactional(readOnly = true)
    public List<IncidenciaResponseDTO> listarPorHuesped(Long huespedId) {
        return incidenciaRepository.findByHuespedIdOrderByFechaCreacionDesc(huespedId)
                .stream()
                .map(i -> toResponse(i, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IncidenciaResponseDTO> listarAbiertasPorHuesped(Long huespedId) {
        List<EstadoIncidencia> estados = List.of(EstadoIncidencia.ABIERTA, EstadoIncidencia.EN_PROCESO);
        return incidenciaRepository.findByHuespedIdAndEstadoInOrderByFechaCreacionDesc(huespedId, estados)
                .stream()
                .map(i -> toResponse(i, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IncidenciaResponseDTO> listarTodasAdmin() {
        return incidenciaRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(i -> toResponse(i, null))
                .toList();
    }

    @Transactional
    public IncidenciaResponseDTO actualizarEstado(Long incidenciaId, ActualizarEstadoIncidenciaRequestDTO dto) {
        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new EntityNotFoundException("Incidencia no encontrada"));

        if (dto.getEstado() == null) {
            throw new IllegalArgumentException("El estado es obligatorio para actualizar la incidencia.");
        }

        incidencia.setEstado(dto.getEstado());

        if (dto.getComentarioResolucion() != null && !dto.getComentarioResolucion().isBlank()) {
            incidencia.setComentarioResolucion(dto.getComentarioResolucion().trim());
        }

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            incidencia.setResueltaPor(usuario);
        }

        if (dto.getEstado() == EstadoIncidencia.RESUELTA && incidencia.getFechaResolucion() == null) {
            incidencia.setFechaResolucion(LocalDateTime.now());
        }

        return toResponse(incidenciaRepository.save(incidencia), null);
    }

    @Transactional
    public IncidenciaResponseDTO resolver(Long incidenciaId, ActualizarEstadoIncidenciaRequestDTO dto) {
        dto.setEstado(EstadoIncidencia.RESUELTA);
        if (dto.getComentarioResolucion() == null || dto.getComentarioResolucion().isBlank()) {
            dto.setComentarioResolucion("Incidencia resuelta por personal.");
        }
        return actualizarEstado(incidenciaId, dto);
    }

    @Transactional
    public IncidenciaResponseDTO asignar(Long incidenciaId, ActualizarEstadoIncidenciaRequestDTO dto) {
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("Para asignar una incidencia se requiere usuarioId.");
        }

        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new EntityNotFoundException("Incidencia no encontrada"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        incidencia.setResueltaPor(usuario);
        if (incidencia.getEstado() == EstadoIncidencia.ABIERTA) {
            incidencia.setEstado(EstadoIncidencia.EN_PROCESO);
        }

        if (dto.getComentarioResolucion() != null && !dto.getComentarioResolucion().isBlank()) {
            incidencia.setComentarioResolucion(dto.getComentarioResolucion().trim());
        }

        return toResponse(incidenciaRepository.save(incidencia), null);
    }

    private IncidenciaResponseDTO toResponse(Incidencia incidencia, EfectosIncidencia efectos) {
        IncidenciaResponseDTO dto = new IncidenciaResponseDTO();
        dto.setId(incidencia.getId());
        dto.setFechaCreacion(incidencia.getFechaCreacion());
        dto.setFechaResolucion(incidencia.getFechaResolucion());
        dto.setCategoria(incidencia.getCategoria());
        dto.setPrioridad(incidencia.getPrioridad());
        dto.setEstado(incidencia.getEstado());
        dto.setDescripcion(incidencia.getDescripcion());
        dto.setFotoUrl(incidencia.getFotoPath());
        dto.setNotificacionEnviada(incidencia.getNotificacionEnviada());
        dto.setTelefonoEmergencia(Boolean.TRUE.equals(incidencia.getTelefonoEmergenciaMostrado()) ? telefonoEmergencia : null);
        dto.setCanalNotificacion(incidencia.getCanalNotificacion());
        dto.setHuespedId(incidencia.getHuesped() != null ? incidencia.getHuesped().getId() : null);
        dto.setReservaId(incidencia.getReserva() != null ? incidencia.getReserva().getId() : null);
        dto.setCapsulaId(incidencia.getCapsula() != null ? incidencia.getCapsula().getId() : null);
        dto.setResueltaPorId(incidencia.getResueltaPor() != null ? incidencia.getResueltaPor().getId() : null);

        if (efectos == null) {
            dto.setMensaje("Incidencia registrada.");
            dto.setCodigoAccesoReenviado(null);
        } else {
            dto.setMensaje(efectos.mensajeResolucion != null
                    ? efectos.mensajeResolucion
                    : "Incidencia recibida. Personal avisado.");
            dto.setCodigoAccesoReenviado(efectos.codigoAcceso);
            if (Boolean.TRUE.equals(efectos.telefonoEmergenciaMostrado)) {
                dto.setTelefonoEmergencia(telefonoEmergencia);
            }
        }

        if (Boolean.FALSE.equals(incidencia.getNotificacionEnviada()) && dto.getMensaje() != null
                && !dto.getMensaje().toLowerCase(Locale.ROOT).contains("no se pudo contactar")) {
            dto.setMensaje("Incidencia registrada, pero no se pudo contactar automáticamente con el personal.");
        }

        return dto;
    }

    private Reserva resolverReservaContexto(CrearIncidenciaRequestDTO dto, Usuario huesped, Reserva reservaActiva) {
        if (dto.getReservaId() != null) {
            Reserva reserva = reservaRepository.findById(dto.getReservaId())
                    .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));
            if (!reserva.getHuesped().getId().equals(huesped.getId())) {
                throw new IllegalArgumentException("La reserva indicada no pertenece al huésped.");
            }
            return reserva;
        }

        return reservaActiva;
    }

    private Capsula resolverCapsulaContexto(CrearIncidenciaRequestDTO dto, Reserva reserva) {
        if (dto.getCapsulaId() != null && !dto.getCapsulaId().isBlank()) {
            return capsulaRepository.findById(dto.getCapsulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Cápsula no encontrada"));
        }

        return reserva != null ? reserva.getCapsula() : null;
    }

    private PrioridadIncidencia calcularPrioridadAutomatica(CategoriaIncidencia categoria, String descripcion) {
        String desc = descripcion == null ? "" : descripcion.toUpperCase(Locale.ROOT);
        boolean critica = desc.contains("FUEGO")
                || desc.contains("HUMO")
                || desc.contains("AGRESION")
                || desc.contains("ROBO")
                || desc.contains("EMERGENCIA");

        boolean afectaHabitabilidad = desc.contains("HABITABILIDAD")
                || desc.contains("INHABITABLE")
                || desc.contains("FUGA")
                || desc.contains("NO FUNCIONA")
                || desc.contains("AVERIA")
                || desc.contains("SIN AGUA")
                || desc.contains("SIN LUZ")
                || desc.contains("NO ABRE")
                || desc.contains("CERRADURA")
                || desc.contains("ACCESO");

        if (categoria == CategoriaIncidencia.SEGURIDAD || categoria == CategoriaIncidencia.ACCESO) {
            return PrioridadIncidencia.URGENTE;
        }

        if (categoria == CategoriaIncidencia.MANTENIMIENTO && (critica || afectaHabitabilidad)) {
            return PrioridadIncidencia.URGENTE;
        }

        if (categoria == CategoriaIncidencia.MANTENIMIENTO) {
            return PrioridadIncidencia.ALTA;
        }

        if (critica) {
            return PrioridadIncidencia.URGENTE;
        }

        return PrioridadIncidencia.NORMAL;
    }

    private EfectosIncidencia ejecutarEfectosSecundarios(Incidencia incidencia) {
        EfectosIncidencia efectos = new EfectosIncidencia();
        CategoriaIncidencia categoria = incidencia.getCategoria();

        if (categoria == CategoriaIncidencia.LIMPIEZA) {
            aplicarReglasLimpieza(incidencia, efectos);
        }

        if (categoria == CategoriaIncidencia.MANTENIMIENTO) {
            aplicarReglasMantenimiento(incidencia);
        }

        if (categoria == CategoriaIncidencia.SEGURIDAD) {
            aplicarReglasSeguridad(incidencia);
        }

        if (categoria == CategoriaIncidencia.ACCESO) {
            aplicarReglasAcceso(incidencia, efectos);
        }

        return efectos;
    }

    private void aplicarReglasLimpieza(Incidencia incidencia, EfectosIncidencia efectos) {
        if (incidencia.getCapsula() == null || incidencia.getReserva() == null) {
            throw new IllegalArgumentException("La incidencia de limpieza requiere cápsula y reserva asociadas.");
        }

        Capsula capsula = incidencia.getCapsula();

        if (incidencia.getPrioridad() == PrioridadIncidencia.URGENTE) {
            capsula.setEstado(EstadoCapsula.BLOQUEADA);
        } else {
            capsula.setEstado(EstadoCapsula.SUCIA);
        }
        capsulaRepository.save(capsula);

        OrdenLimpieza orden = new OrdenLimpieza();
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado(EstadoOrdenLimpieza.PENDIENTE);
        orden.setMensaje("Incidencia de limpieza reportada: " + incidencia.getDescripcion());
        orden.setCapsula(capsula);
        orden.setReserva(incidencia.getReserva());
        ordenLimpiezaRepository.save(orden);

        if (Boolean.TRUE.equals(incidencia.getReserva().getCheckInRealizado())) {
            intentarReasignacionAutomatica(incidencia.getReserva(), capsula, efectos);
        }
    }

    private void aplicarReglasMantenimiento(Incidencia incidencia) {
        if (incidencia.getCapsula() == null) {
            return;
        }

        if (incidencia.getPrioridad() == PrioridadIncidencia.URGENTE) {
            Capsula capsula = incidencia.getCapsula();
            capsula.setEstado(EstadoCapsula.BLOQUEADA);
            capsulaRepository.save(capsula);
        }
    }

    private void aplicarReglasSeguridad(Incidencia incidencia) {
        incidencia.setPrioridad(PrioridadIncidencia.URGENTE);

        if (incidencia.getCapsula() != null) {
            Capsula capsula = incidencia.getCapsula();
            capsula.setEstado(EstadoCapsula.BLOQUEADA);
            capsulaRepository.save(capsula);
        }
    }

    private void aplicarReglasAcceso(Incidencia incidencia, EfectosIncidencia efectos) {
        incidencia.setPrioridad(PrioridadIncidencia.URGENTE);

        Reserva reserva = incidencia.getReserva();
        if (reserva == null
                || reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.FINALIZADA
                || reserva.getFechaSalida() != null
                || !Boolean.TRUE.equals(reserva.getCheckInRealizado())) {
            efectos.resueltaAutomaticamente = false;
            efectos.telefonoEmergenciaMostrado = true;
            efectos.mensajeResolucion = "No se pudo resolver automáticamente el acceso.";
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        boolean caducado = reserva.getAccesoValidoHasta() == null || ahora.isAfter(reserva.getAccesoValidoHasta());

        if (reserva.getCodigoAcceso() == null || reserva.getCodigoAcceso().isBlank() || caducado) {
            reserva.setCodigoAcceso(generarCodigoAcceso());
            reserva.setAccesoValidoHasta(reserva.getFechaFinal().atTime(12, 0));
            reservaRepository.save(reserva);
            efectos.mensajeResolucion = "Se regeneró automáticamente el código de acceso.";
        } else {
            efectos.mensajeResolucion = "Se devolvió el código de acceso vigente.";
        }

        efectos.resueltaAutomaticamente = true;
        efectos.codigoAcceso = reserva.getCodigoAcceso();
        efectos.accesoValidoHasta = reserva.getAccesoValidoHasta();
    }

    private void intentarReasignacionAutomatica(Reserva reserva, Capsula capsulaOriginal, EfectosIncidencia efectos) {
        LocalDate fechaInicio = reserva.getFechaInicio();
        LocalDate fechaFinal = reserva.getFechaFinal();
        String categoriaObjetivo = capsulaOriginal.getCategoria();

        if (categoriaObjetivo == null || categoriaObjetivo.isBlank()) {
            categoriaObjetivo = "STANDARD";
        }

        Capsula nuevaCapsula = capsulaRepository
                .findDisponiblesByCategoriaBetween(categoriaObjetivo, fechaInicio, fechaFinal)
                .stream()
                .filter(c -> !c.getId().equals(capsulaOriginal.getId()))
                .findFirst()
                .orElse(null);

        if (nuevaCapsula == null) {
            efectos.resueltaAutomaticamente = false;
            efectos.mensajeResolucion = "No se encontró una cápsula alternativa disponible de la misma categoría.";
            return;
        }

        reserva.setCapsula(nuevaCapsula);
        reservaRepository.save(reserva);

        efectos.resueltaAutomaticamente = true;
        efectos.capsulaReasignadaId = nuevaCapsula.getId();
        efectos.mensajeResolucion = "Reserva reasignada automáticamente a la cápsula " + nuevaCapsula.getId() + ".";
    }

    private String normalizarCanal(String canal) {
        if (canal == null || canal.isBlank()) {
            return "BACKOFFICE";
        }
        return canal.trim().toUpperCase(Locale.ROOT);
    }

    private boolean intentarNotificarPersonal(Incidencia incidencia, boolean notificacionInmediata) {
        if (notificacionInmediata) {
            System.out.println("[Incidencias] Alerta inmediata de seguridad para personal/admin");
        }

        if ("SIN_NOTIFICACION".equals(incidencia.getCanalNotificacion())) {
            throw new IllegalStateException("Notificación deshabilitada para este canal");
        }

        System.out.println(
                "[Incidencias] Notificación enviada por "
                        + incidencia.getCanalNotificacion()
                        + " para incidencia #"
                        + incidencia.getId()
        );
        return true;
    }

    private String generarCodigoAcceso() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase(Locale.ROOT);
    }

    private static class EfectosIncidencia {
        private Boolean resueltaAutomaticamente = false;
        private Boolean telefonoEmergenciaMostrado = false;
        private String capsulaReasignadaId;
        private String codigoAcceso;
        private LocalDateTime accesoValidoHasta;
        private String mensajeResolucion;
    }
}
