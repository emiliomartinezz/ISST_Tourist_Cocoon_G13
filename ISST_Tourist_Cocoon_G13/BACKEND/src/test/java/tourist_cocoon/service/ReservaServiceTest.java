package tourist_cocoon.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.enums.EstadoCapsula;
import tourist_cocoon.model.enums.EstadoReserva;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.OrdenLimpiezaRepository;
import tourist_cocoon.repository.ReservaRepository;
import tourist_cocoon.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de ReservaService.
 * Se testea validarReserva(), que contiene las reglas de negocio más críticas.
 * Mockito simula los repositorios: no se accede a la base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private CapsulaRepository capsulaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private OrdenLimpiezaRepository ordenLimpiezaRepository;
    @Mock private GoogleCalendarService googleCalendarService;
    @Mock private StripeService stripeService;

    @InjectMocks
    private ReservaService reservaService;

    private static final LocalDate INICIO  = LocalDate.of(2025, 8, 1);
    private static final LocalDate FIN_OK  = LocalDate.of(2025, 8, 4); // 3 noches
    private static final String CAPSULA_ID = "C-101";
    private static final Long   HUESPED_ID = 1L;

    // ─────────────────────────────────────────────────
    //  Validaciones de fechas
    // ─────────────────────────────────────────────────

    @Test
    void validarReserva_fechaSalidaIgualEntrada_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, INICIO));
    }

    @Test
    void validarReserva_fechaSalidaAntesFechaEntrada_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, INICIO.minusDays(1)));
    }

    @Test
    void validarReserva_masDeSetteNochesConsecutivas_lanzaExcepcion() {
        // 8 noches supera el límite de 7
        LocalDate fin = INICIO.plusDays(8);
        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, fin));
    }

    // ─────────────────────────────────────────────────
    //  Límite mensual (15 noches / mes)
    // ─────────────────────────────────────────────────

    @Test
    void validarReserva_superaLimiteMensual_lanzaExcepcion() {
        // El huésped ya tiene 13 noches reservadas en agosto
        Reserva existente = reservaConFechas(
            LocalDate.of(2025, 8, 10),
            LocalDate.of(2025, 8, 23) // 13 noches
        );
        when(reservaRepository.findByHuespedId(HUESPED_ID)).thenReturn(List.of(existente));

        // Añadir 3 noches más = 16 > 15 → debe fallar
        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, FIN_OK));
    }

    // ─────────────────────────────────────────────────
    //  Solapamiento de reservas
    // ─────────────────────────────────────────────────

    @Test
    void validarReserva_existeReservaSolapada_lanzaExcepcion() {
        when(reservaRepository.findByHuespedId(HUESPED_ID)).thenReturn(List.of());
        when(reservaRepository.findReservasActivasSolapadas(eq(HUESPED_ID), any(), any()))
            .thenReturn(List.of(new Reserva())); // devuelve un solapamiento

        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, FIN_OK));
    }

    // ─────────────────────────────────────────────────
    //  Disponibilidad de cápsula
    // ─────────────────────────────────────────────────

    @Test
    void validarReserva_capsulaNoDisponible_lanzaExcepcion() {
        when(reservaRepository.findByHuespedId(HUESPED_ID)).thenReturn(List.of());
        when(reservaRepository.findReservasActivasSolapadas(any(), any(), any())).thenReturn(List.of());
        // La cápsula disponible es otra distinta a CAPSULA_ID
        Capsula otraCapsula = capsula("C-999");
        when(capsulaRepository.findDisponiblesBetween(any(), any())).thenReturn(List.of(otraCapsula));

        assertThrows(IllegalArgumentException.class, () ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, FIN_OK));
    }

    // ─────────────────────────────────────────────────
    //  Caso feliz: reserva completamente válida
    // ─────────────────────────────────────────────────

    @Test
    void validarReserva_datosCorrectos_noLanzaExcepcion() {
        when(reservaRepository.findByHuespedId(HUESPED_ID)).thenReturn(List.of());
        when(reservaRepository.findReservasActivasSolapadas(any(), any(), any())).thenReturn(List.of());
        when(capsulaRepository.findDisponiblesBetween(any(), any())).thenReturn(List.of(capsula(CAPSULA_ID)));

        assertDoesNotThrow(() ->
            reservaService.validarReserva(HUESPED_ID, CAPSULA_ID, INICIO, FIN_OK));
    }

    // ─────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────

    private Capsula capsula(String id) {
        Capsula c = new Capsula();
        c.setId(id);
        c.setPlanta(1);
        c.setEstado(EstadoCapsula.DISPONIBLE);
        return c;
    }

    private Reserva reservaConFechas(LocalDate inicio, LocalDate fin) {
        Reserva r = new Reserva();
        r.setFechaInicio(inicio);
        r.setFechaFinal(fin);
        r.setEstado(EstadoReserva.CONFIRMADA);
        return r;
    }
}
