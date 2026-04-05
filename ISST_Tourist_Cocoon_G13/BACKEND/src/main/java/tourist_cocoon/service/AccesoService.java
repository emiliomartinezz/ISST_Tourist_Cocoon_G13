package tourist_cocoon.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tourist_cocoon.dto.RegistroAccesoAdminDTO;
import tourist_cocoon.dto.SolicitudAccesoRequestDTO;
import tourist_cocoon.dto.SolicitudAccesoResponseDTO;
import tourist_cocoon.model.RegistroAcceso;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.RegistroAccesoRepository;
import tourist_cocoon.repository.UsuarioRepository;

@Service
public class AccesoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private RegistroAccesoRepository registroAccesoRepository;

    @Transactional
    public SolicitudAccesoResponseDTO solicitarAcceso(SolicitudAccesoRequestDTO dto) {
        Usuario huesped = usuarioRepository.findById(dto.getHuespedId())
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));

        Reserva reservaActiva = reservaService.reservaActiva(dto.getHuespedId());

        String puerta = dto.getPuerta().trim().toUpperCase();
        String credencial = dto.getCredencial().trim().toUpperCase();
        String capsulaSolicitada = dto.getCapsulaId();
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Debe existir una estancia activa válida
        if (reservaActiva == null
                || "FINALIZADA".equalsIgnoreCase(reservaActiva.getEstado())
                || reservaActiva.getFechaSalida() != null) {

            registrar(
                    huesped,
                    null,
                    puerta,
                    capsulaSolicitada,
                    credencial,
                    "DENEGADO",
                    "No existe una estancia activa válida",
                    ahora
            );

            return new SolicitudAccesoResponseDTO(
                    false,
                    "DENEGADO",
                    "No tienes una estancia activa válida",
                    puerta,
                    obtenerObjetivo(puerta, capsulaSolicitada),
                    ahora
            );
        }

        // 2. El check-in debe haberse realizado previamente
        if (!Boolean.TRUE.equals(reservaActiva.getCheckInRealizado())) {
            registrar(
                    huesped,
                    reservaActiva,
                    puerta,
                    capsulaSolicitada,
                    credencial,
                    "DENEGADO",
                    "El huésped no ha realizado el check-in",
                    ahora
            );

            return new SolicitudAccesoResponseDTO(
                    false,
                    "DENEGADO",
                    "Debes realizar el check-in antes de acceder a las instalaciones",
                    puerta,
                    obtenerObjetivo(puerta, capsulaSolicitada),
                    ahora
            );
        }

        // 3. La credencial no debe estar caducada
        if (reservaActiva.getAccesoValidoHasta() != null
                && ahora.isAfter(reservaActiva.getAccesoValidoHasta())) {

            registrar(
                    huesped,
                    reservaActiva,
                    puerta,
                    capsulaSolicitada,
                    credencial,
                    "DENEGADO",
                    "La credencial ha expirado",
                    ahora
            );

            return new SolicitudAccesoResponseDTO(
                    false,
                    "DENEGADO",
                    "Tu acceso ha caducado",
                    puerta,
                    obtenerObjetivo(puerta, capsulaSolicitada),
                    ahora
            );
        }

        // 4. Acceso al edificio
        if ("EDIFICIO".equals(puerta)) {
            registrar(
                    huesped,
                    reservaActiva,
                    puerta,
                    null,
                    credencial,
                    "EXITO",
                    "Acceso permitido al edificio",
                    ahora
            );

            return new SolicitudAccesoResponseDTO(
                    true,
                    "EXITO",
                    "Puerta principal abierta",
                    puerta,
                    "PUERTA_PRINCIPAL",
                    ahora
            );
        }

        // 5. Acceso a la cápsula
        if ("CAPSULA".equals(puerta)) {
            String capsulaReserva = reservaActiva.getCapsula().getId();

            if (capsulaSolicitada == null || !capsulaReserva.equalsIgnoreCase(capsulaSolicitada)) {
                registrar(
                        huesped,
                        reservaActiva,
                        puerta,
                        capsulaSolicitada,
                        credencial,
                        "DENEGADO",
                        "Intento de acceso a una cápsula no autorizada",
                        ahora
                );

                return new SolicitudAccesoResponseDTO(
                        false,
                        "DENEGADO",
                        "No tienes permisos para abrir esa cápsula",
                        puerta,
                        capsulaSolicitada,
                        ahora
                );
            }

            registrar(
                    huesped,
                    reservaActiva,
                    puerta,
                    capsulaSolicitada,
                    credencial,
                    "EXITO",
                    "Acceso permitido a la cápsula asignada",
                    ahora
            );

            return new SolicitudAccesoResponseDTO(
                    true,
                    "EXITO",
                    "Cápsula abierta correctamente",
                    puerta,
                    capsulaSolicitada,
                    ahora
            );
        }

        // 6. Tipo de puerta no válido
        registrar(
                huesped,
                reservaActiva,
                puerta,
                capsulaSolicitada,
                credencial,
                "DENEGADO",
                "Tipo de puerta no válido",
                ahora
        );

        return new SolicitudAccesoResponseDTO(
                false,
                "DENEGADO",
                "Tipo de puerta no válido",
                puerta,
                obtenerObjetivo(puerta, capsulaSolicitada),
                ahora
        );
    }

    private void registrar(
            Usuario huesped,
            Reserva reserva,
            String puerta,
            String capsulaId,
            String credencial,
            String resultado,
            String motivo,
            LocalDateTime fechaHora
    ) {
        RegistroAcceso registro = new RegistroAcceso();
        registro.setFechaHora(fechaHora);
        registro.setPuerta(puerta);
        registro.setResultado(resultado);
        registro.setCredencial(credencial);
        registro.setObjetivo(obtenerObjetivo(puerta, capsulaId));
        registro.setMotivo(motivo);
        registro.setHuesped(huesped);
        registro.setReserva(reserva);

        registroAccesoRepository.save(registro);
    }

    private String obtenerObjetivo(String puerta, String capsulaId) {
        if ("EDIFICIO".equalsIgnoreCase(puerta)) {
            return "PUERTA_PRINCIPAL";
        }
        return capsulaId;
    }

        @Transactional(readOnly = true)
        public List<RegistroAccesoAdminDTO> listarTodosLosRegistros() {
                return registroAccesoRepository.findAllByOrderByFechaHoraDesc()
                                .stream()
                                .map(r -> new RegistroAccesoAdminDTO(
                                                r.getId(),
                                                r.getFechaHora(),
                                                r.getPuerta(),
                                                r.getResultado(),
                                                r.getCredencial(),
                                                r.getObjetivo(),
                                                r.getMotivo(),
                                                r.getHuesped() != null ? r.getHuesped().getId() : null,
                                                r.getHuesped() != null ? r.getHuesped().getNombre() : null,
                                                r.getHuesped() != null ? r.getHuesped().getEmail() : null,
                                                r.getReserva() != null ? r.getReserva().getId() : null
                                ))
                                .toList();
        }
}