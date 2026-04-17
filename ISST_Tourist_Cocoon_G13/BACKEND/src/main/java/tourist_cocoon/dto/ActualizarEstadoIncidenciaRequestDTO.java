package tourist_cocoon.dto;

import jakarta.validation.constraints.Size;
import tourist_cocoon.model.enums.EstadoIncidencia;

public class ActualizarEstadoIncidenciaRequestDTO {

    private EstadoIncidencia estado;

    @Size(max = 2000, message = "El comentario no puede superar 2000 caracteres")
    private String comentarioResolucion;

    private Long usuarioId;

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
