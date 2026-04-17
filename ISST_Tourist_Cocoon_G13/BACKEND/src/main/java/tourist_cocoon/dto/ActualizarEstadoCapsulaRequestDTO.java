package tourist_cocoon.dto;

import jakarta.validation.constraints.NotNull;
import tourist_cocoon.model.enums.EstadoCapsula;

public class ActualizarEstadoCapsulaRequestDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoCapsula estado;

    public EstadoCapsula getEstado() { return estado; }
    public void setEstado(EstadoCapsula estado) { this.estado = estado; }
}
