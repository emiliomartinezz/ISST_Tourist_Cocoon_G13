package tourist_cocoon.dto;

import jakarta.validation.constraints.NotNull;

public class CancelarReservaRequestDTO {
    @NotNull(message = "El id del huésped es obligatorio")
    private Long huespedId;

    public Long getHuespedId() { return huespedId; }
    public void setHuespedId(Long huespedId) { this.huespedId = huespedId; }
}
