package tourist_cocoon.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tourist_cocoon.model.converter.EstadoReservaConverter;
import tourist_cocoon.model.enums.EstadoReserva;

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

    @Column(nullable = true)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    @Convert(converter = EstadoReservaConverter.class)
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;

    private String googleCalendarEventId;

    private String googleCalendarEventIdCliente;

    private String stripePaymentIntentId;

    @Column(nullable = false)
    private Boolean checkInRealizado = false;

    private LocalDateTime fechaCheckIn;

    private String documentoIdentidad;

    @Column(nullable = false)
    private Boolean documentoValidado = false;

    @Column(nullable = false)
    private Boolean datosAutoridadEnviados = false;

    private String codigoAcceso;

    private LocalDateTime accesoValidoHasta;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Usuario huesped;

    @ManyToOne
    @JoinColumn(name = "capsula_id", nullable = false)
    private Capsula capsula;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(LocalDate fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getGoogleCalendarEventId() {
        return googleCalendarEventId;
    }

    public void setGoogleCalendarEventId(String googleCalendarEventId) {
        this.googleCalendarEventId = googleCalendarEventId;
    }

    public String getGoogleCalendarEventIdCliente() {
        return googleCalendarEventIdCliente;
    }

    public void setGoogleCalendarEventIdCliente(String googleCalendarEventIdCliente) {
        this.googleCalendarEventIdCliente = googleCalendarEventIdCliente;
    }

    public Boolean getCheckInRealizado() {
        return checkInRealizado;
    }

    public void setCheckInRealizado(Boolean checkInRealizado) {
        this.checkInRealizado = checkInRealizado;
    }

    public LocalDateTime getFechaCheckIn() {
        return fechaCheckIn;
    }

    public void setFechaCheckIn(LocalDateTime fechaCheckIn) {
        this.fechaCheckIn = fechaCheckIn;
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

    public Boolean getDatosAutoridadEnviados() {
        return datosAutoridadEnviados;
    }

    public void setDatosAutoridadEnviados(Boolean datosAutoridadEnviados) {
        this.datosAutoridadEnviados = datosAutoridadEnviados;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public LocalDateTime getAccesoValidoHasta() {
        return accesoValidoHasta;
    }

    public void setAccesoValidoHasta(LocalDateTime accesoValidoHasta) {
        this.accesoValidoHasta = accesoValidoHasta;
    }

    public Usuario getHuesped() {
        return huesped;
    }

    public void setHuesped(Usuario huesped) {
        this.huesped = huesped;
    }

    public Capsula getCapsula() {
        return capsula;
    }

    public void setCapsula(Capsula capsula) {
        this.capsula = capsula;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }
}