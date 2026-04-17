package tourist_cocoon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import tourist_cocoon.model.enums.CategoriaIncidencia;
import tourist_cocoon.model.enums.EstadoIncidencia;
import tourist_cocoon.model.enums.PrioridadIncidencia;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaResolucion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoriaIncidencia categoria;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PrioridadIncidencia prioridad = PrioridadIncidencia.NORMAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoIncidencia estado = EstadoIncidencia.ABIERTA;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    private String fotoPath;

    @Column(nullable = false)
    private Boolean notificacionEnviada = false;

    private String canalNotificacion;

    @Column(nullable = false)
    private Boolean telefonoEmergenciaMostrado = false;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Usuario huesped;

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "capsula_id")
    private Capsula capsula;

    @Column(length = 2000)
    private String comentarioResolucion;

    @ManyToOne
    @JoinColumn(name = "resuelta_por")
    private Usuario resueltaPor;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

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

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public Boolean getNotificacionEnviada() {
        return notificacionEnviada;
    }

    public void setNotificacionEnviada(Boolean notificacionEnviada) {
        this.notificacionEnviada = notificacionEnviada;
    }

    public String getCanalNotificacion() {
        return canalNotificacion;
    }

    public void setCanalNotificacion(String canalNotificacion) {
        this.canalNotificacion = canalNotificacion;
    }

    public Boolean getTelefonoEmergenciaMostrado() {
        return telefonoEmergenciaMostrado;
    }

    public void setTelefonoEmergenciaMostrado(Boolean telefonoEmergenciaMostrado) {
        this.telefonoEmergenciaMostrado = telefonoEmergenciaMostrado;
    }

    public Usuario getHuesped() {
        return huesped;
    }

    public void setHuesped(Usuario huesped) {
        this.huesped = huesped;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Capsula getCapsula() {
        return capsula;
    }

    public void setCapsula(Capsula capsula) {
        this.capsula = capsula;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }

    public Usuario getResueltaPor() {
        return resueltaPor;
    }

    public void setResueltaPor(Usuario resueltaPor) {
        this.resueltaPor = resueltaPor;
    }
}
