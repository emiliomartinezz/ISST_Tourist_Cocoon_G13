package tourist_cocoon.dto;

import java.time.LocalDateTime;

public class CheckInResponseDTO {

    private Long reservaId;
    private boolean checkInRealizado;
    private String mensaje;
    private String capsulaId;
    private String codigoAcceso;
    private LocalDateTime fechaCheckIn;
    private LocalDateTime accesoValidoHasta;
    private Boolean datosAutoridadEnviados;

    public CheckInResponseDTO() {
    }

    public CheckInResponseDTO(
            Long reservaId,
            boolean checkInRealizado,
            String mensaje,
            String capsulaId,
            String codigoAcceso,
            LocalDateTime fechaCheckIn,
            LocalDateTime accesoValidoHasta,
            Boolean datosAutoridadEnviados
    ) {
        this.reservaId = reservaId;
        this.checkInRealizado = checkInRealizado;
        this.mensaje = mensaje;
        this.capsulaId = capsulaId;
        this.codigoAcceso = codigoAcceso;
        this.fechaCheckIn = fechaCheckIn;
        this.accesoValidoHasta = accesoValidoHasta;
        this.datosAutoridadEnviados = datosAutoridadEnviados;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public boolean isCheckInRealizado() {
        return checkInRealizado;
    }

    public void setCheckInRealizado(boolean checkInRealizado) {
        this.checkInRealizado = checkInRealizado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCapsulaId() {
        return capsulaId;
    }

    public void setCapsulaId(String capsulaId) {
        this.capsulaId = capsulaId;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public LocalDateTime getFechaCheckIn() {
        return fechaCheckIn;
    }

    public void setFechaCheckIn(LocalDateTime fechaCheckIn) {
        this.fechaCheckIn = fechaCheckIn;
    }

    public LocalDateTime getAccesoValidoHasta() {
        return accesoValidoHasta;
    }

    public void setAccesoValidoHasta(LocalDateTime accesoValidoHasta) {
        this.accesoValidoHasta = accesoValidoHasta;
    }

    public Boolean getDatosAutoridadEnviados() {
        return datosAutoridadEnviados;
    }

    public void setDatosAutoridadEnviados(Boolean datosAutoridadEnviados) {
        this.datosAutoridadEnviados = datosAutoridadEnviados;
    }
}