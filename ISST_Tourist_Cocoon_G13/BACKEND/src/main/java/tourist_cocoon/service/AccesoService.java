package tourist_cocoon.service;

import java.time.LocalDateTime;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}