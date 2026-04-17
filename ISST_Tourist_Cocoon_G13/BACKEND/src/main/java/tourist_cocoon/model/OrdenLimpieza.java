package tourist_cocoon.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import tourist_cocoon.model.converter.EstadoOrdenLimpiezaConverter;
import tourist_cocoon.model.enums.EstadoOrdenLimpieza;

@Entity
@Table(name = "ordenes_limpieza")
public class OrdenLimpieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @Convert(converter = EstadoOrdenLimpiezaConverter.class)
    private EstadoOrdenLimpieza estado;

    @Column(nullable = false)
    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "capsula_id", nullable = false)
    private Capsula capsula;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    public OrdenLimpieza() {
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoOrdenLimpieza getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrdenLimpieza estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Capsula getCapsula() {
        return capsula;
    }

    public void setCapsula(Capsula capsula) {
        this.capsula = capsula;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}