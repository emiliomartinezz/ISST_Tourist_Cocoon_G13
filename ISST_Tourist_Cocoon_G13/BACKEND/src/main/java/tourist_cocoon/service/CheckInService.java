package tourist_cocoon.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;

import tourist_cocoon.dto.CheckInRequestDTO;
import tourist_cocoon.dto.CheckInResponseDTO;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.repository.ReservaRepository;

@Service
public class CheckInService {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Transactional
    public CheckInResponseDTO realizarCheckIn(CheckInRequestDTO dto) {
        Reserva reservaActiva = reservaService.reservaActiva(dto.getHuespedId());

        if (reservaActiva == null) {
            throw new ErrorResponseException(
                    HttpStatus.BAD_REQUEST,
                    ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            "No existe una reserva activa válida para realizar el check-in"
                    ),
                    null
            );
        }

        if (Boolean.TRUE.equals(reservaActiva.getCheckInRealizado())) {
            return new CheckInResponseDTO(
                    reservaActiva.getId(),
                    true,
                    "El check-in ya estaba realizado",
                    reservaActiva.getCapsula().getId(),
                    reservaActiva.getCodigoAcceso(),
                    reservaActiva.getFechaCheckIn(),
                    reservaActiva.getAccesoValidoHasta(),
                    reservaActiva.getDatosAutoridadEnviados()
            );
        }

        if (!Boolean.TRUE.equals(dto.getDocumentoValidado())) {
            throw new ErrorResponseException(
                    HttpStatus.BAD_REQUEST,
                    ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            "No se ha podido validar el documento de identidad"
                    ),
                    null
            );
        }

        LocalDateTime ahora = LocalDateTime.now();

        reservaActiva.setDocumentoIdentidad(dto.getDocumentoIdentidad().trim());
        reservaActiva.setDocumentoValidado(true);
        reservaActiva.setCheckInRealizado(true);
        reservaActiva.setFechaCheckIn(ahora);
        reservaActiva.setDatosAutoridadEnviados(true);
        reservaActiva.setCodigoAcceso(generarCodigoAcceso());
        reservaActiva.setAccesoValidoHasta(reservaActiva.getFechaFinal().atTime(12, 0));

        if (reservaActiva.getCapsula() != null) {
            reservaActiva.getCapsula().setEstado("OCUPADA");
        }

        Reserva saved = reservaRepository.save(reservaActiva);

        return new CheckInResponseDTO(
                saved.getId(),
                true,
                "Check-in realizado correctamente. Acceso habilitado.",
                saved.getCapsula().getId(),
                saved.getCodigoAcceso(),
                saved.getFechaCheckIn(),
                saved.getAccesoValidoHasta(),
                saved.getDatosAutoridadEnviados()
        );
    }

    private String generarCodigoAcceso() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}