package tourist_cocoon.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReservaRequestDTO {
    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFinal;

    @NotNull
    private Long huespedId;

    @NotBlank
    private String capsulaId;

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDate fechaFinal) { this.fechaFinal = fechaFinal; }
    public Long getHuespedId() { return huespedId; }
    public void setHuespedId(Long huespedId) { this.huespedId = huespedId; }
    public String getCapsulaId() { return capsulaId; }
    public void setCapsulaId(String capsulaId) { this.capsulaId = capsulaId; }

    @AssertTrue(message = "La fecha final debe ser posterior a la fecha de inicio")
    public boolean isRangoValido() {
        return fechaInicio != null && fechaFinal != null && fechaFinal.isAfter(fechaInicio);
    }
}
