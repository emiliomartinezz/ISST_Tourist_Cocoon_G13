package tourist_cocoon.dto;

import java.time.LocalDateTime;

public record RegistroAccesoAdminDTO(
    Long id,
    LocalDateTime fechaHora,
    String puerta,
    String resultado,
    String credencial,
    String objetivo,
    String motivo,
    Long huespedId,
    String huespedNombre,
    String huespedEmail,
    Long reservaId
) {}