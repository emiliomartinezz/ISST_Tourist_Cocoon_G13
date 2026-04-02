package tourist_cocoon.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CheckoutRequestDTO {

    @NotNull(message = "El id del huésped es obligatorio")
    private Long huespedId;

    private LocalDate fechaSalida;

    public Long getHuespedId() {
        return huespedId;
    }

    public void setHuespedId(Long huespedId) {
        this.huespedId = huespedId;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}