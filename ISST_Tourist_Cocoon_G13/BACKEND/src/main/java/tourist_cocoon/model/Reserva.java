package tourist_cocoon.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFinal;

    @Column(nullable = false)
    private String estado = "CONFIRMADA";

    private String googleCalendarEventId;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Usuario huesped;

    @ManyToOne
    @JoinColumn(name = "capsula_id", nullable = false)
    private Capsula capsula;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDate fechaFinal) { this.fechaFinal = fechaFinal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getGoogleCalendarEventId() { return googleCalendarEventId; }
    public void setGoogleCalendarEventId(String googleCalendarEventId) { this.googleCalendarEventId = googleCalendarEventId; }
    public Usuario getHuesped() { return huesped; }
    public void setHuesped(Usuario huesped) { this.huesped = huesped; }
    public Capsula getCapsula() { return capsula; }
    public void setCapsula(Capsula capsula) { this.capsula = capsula; }
}