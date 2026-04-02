package tourist_cocoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SolicitudAccesoRequestDTO {

    @NotNull(message = "El id del huésped es obligatorio")
    private Long huespedId;

    @NotBlank(message = "La puerta es obligatoria")
    private String puerta; // EDIFICIO o CAPSULA

    private String capsulaId;

    @NotBlank(message = "La credencial es obligatoria")
    private String credencial; // APP por ahora

    public Long getHuespedId() {
        return huespedId;
    }

    public void setHuespedId(Long huespedId) {
        this.huespedId = huespedId;
    }

    public String getPuerta() {
        return puerta;
    }

    public void setPuerta(String puerta) {
        this.puerta = puerta;
    }

    public String getCapsulaId() {
        return capsulaId;
    }

    public void setCapsulaId(String capsulaId) {
        this.capsulaId = capsulaId;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }
}