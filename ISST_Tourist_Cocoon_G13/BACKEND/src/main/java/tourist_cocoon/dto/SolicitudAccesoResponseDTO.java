package tourist_cocoon.dto;

import java.time.LocalDateTime;

public class SolicitudAccesoResponseDTO {

    private boolean autorizado;
    private String resultado;
    private String mensaje;
    private String puerta;
    private String objetivo;
    private LocalDateTime timestamp;

    public SolicitudAccesoResponseDTO() {
    }

    public SolicitudAccesoResponseDTO(
            boolean autorizado,
            String resultado,
            String mensaje,
            String puerta,
            String objetivo,
            LocalDateTime timestamp
    ) {
        this.autorizado = autorizado;
        this.resultado = resultado;
        this.mensaje = mensaje;
        this.puerta = puerta;
        this.objetivo = objetivo;
        this.timestamp = timestamp;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getPuerta() {
        return puerta;
    }

    public void setPuerta(String puerta) {
        this.puerta = puerta;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}