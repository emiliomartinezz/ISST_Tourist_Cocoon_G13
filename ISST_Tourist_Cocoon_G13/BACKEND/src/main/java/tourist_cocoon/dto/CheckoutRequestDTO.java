package tourist_cocoon.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CheckoutRequestDTO {
    @NotNull
    private LocalDate fechaSalida;

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
}
