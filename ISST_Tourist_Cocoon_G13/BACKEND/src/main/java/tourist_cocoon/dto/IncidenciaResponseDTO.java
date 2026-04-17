package tourist_cocoon.dto;

import java.time.LocalDateTime;
import tourist_cocoon.model.enums.CategoriaIncidencia;
import tourist_cocoon.model.enums.EstadoIncidencia;
import tourist_cocoon.model.enums.PrioridadIncidencia;

public class IncidenciaResponseDTO {

    private Long id;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private CategoriaIncidencia categoria;
    private PrioridadIncidencia prioridad;
    private EstadoIncidencia estado;
    private String descripcion;
    private String fotoUrl;
    private Boolean notificacionEnviada;
    private String telefonoEmergencia;
    private String mensaje;
    private String codigoAccesoReenviado;
    private String canalNotificacion;
    private Long huespedId;
    private Long reservaId;
    private String capsulaId;
    private Long resueltaPorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public CategoriaIncidencia getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaIncidencia categoria) {
        this.categoria = categoria;
    }

    public PrioridadIncidencia getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadIncidencia prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Boolean getNotificacionEnviada() {
        return notificacionEnviada;
    }

    public void setNotificacionEnviada(Boolean notificacionEnviada) {
        this.notificacionEnviada = notificacionEnviada;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigoAccesoReenviado() {
        return codigoAccesoReenviado;
    }

    public void setCodigoAccesoReenviado(String codigoAccesoReenviado) {
        this.codigoAccesoReenviado = codigoAccesoReenviado;
    }

    public String getCanalNotificacion() {
        return canalNotificacion;
    }

    public void setCanalNotificacion(String canalNotificacion) {
        this.canalNotificacion = canalNotificacion;
    }

    public Long getHuespedId() {
        return huespedId;
    }

    public void setHuespedId(Long huespedId) {
        this.huespedId = huespedId;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public String getCapsulaId() {
        return capsulaId;
    }

    public void setCapsulaId(String capsulaId) {
        this.capsulaId = capsulaId;
    }

    public Long getResueltaPorId() {
        return resueltaPorId;
    }

    public void setResueltaPorId(Long resueltaPorId) {
        this.resueltaPorId = resueltaPorId;
    }
}
