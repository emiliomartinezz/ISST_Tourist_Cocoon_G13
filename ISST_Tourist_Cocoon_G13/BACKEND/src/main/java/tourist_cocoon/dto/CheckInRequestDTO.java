package tourist_cocoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckInRequestDTO {

    @NotNull(message = "El id del huésped es obligatorio")
    private Long huespedId;

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    @NotNull(message = "Debes indicar si el documento ha sido validado")
    private Boolean documentoValidado;

    public Long getHuespedId() {
        return huespedId;
    }

    public void setHuespedId(Long huespedId) {
        this.huespedId = huespedId;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public Boolean getDocumentoValidado() {
        return documentoValidado;
    }

    public void setDocumentoValidado(Boolean documentoValidado) {
        this.documentoValidado = documentoValidado;
    }
}