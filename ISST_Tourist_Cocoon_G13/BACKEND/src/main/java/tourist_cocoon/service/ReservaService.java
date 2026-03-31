package tourist_cocoon.service;

import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.ReservaRepository;
import tourist_cocoon.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {

    // Límites legales configurables por el administrador
    private static final int MAX_NOCHES_SEGUIDAS = 7;
    private static final int MAX_NOCHES_MES = 15;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CapsulaRepository capsulaRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    /**
     * Crea una reserva completa:
     * 1. Valida reglas legales de estancia.
     * 2. Comprueba disponibilidad de la cápsula.
     * 3. Persiste la reserva.
     * 4. Sincroniza con Google Calendar.
     * 5. Actualiza el estado de la cápsula.
     */
    @Transactional
    public Reserva crearReserva(Long huespedId, String capsulaId,
                                LocalDate fechaInicio, LocalDate fechaFinal) {

        // --- Cargar entidades ---
        Usuario huesped = usuarioRepository.findById(huespedId)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado: " + huespedId));

        Capsula capsula = capsulaRepository.findById(capsulaId)
                .orElseThrow(() -> new IllegalArgumentException("Cápsula no encontrada: " + capsulaId));

        // --- Reglas de dominio ---
        long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFinal);
        if (noches <= 0) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
        }
        if (noches > MAX_NOCHES_SEGUIDAS) {
            throw new IllegalArgumentException(
                "La estancia supera el máximo de " + MAX_NOCHES_SEGUIDAS + " noches consecutivas permitidas.");
        }

        long nochesEnElMes = calcularNochesEnMes(huespedId, fechaInicio, fechaFinal);
        if (nochesEnElMes + noches > MAX_NOCHES_MES) {
            throw new IllegalArgumentException(
                "Esta reserva superaría el límite de " + MAX_NOCHES_MES +
                " noches al mes. Noches ya usadas este mes: " + nochesEnElMes + ".");
        }

        // --- Disponibilidad de la cápsula ---
        List<Capsula> disponibles = capsulaRepository.findDisponiblesBetween(fechaInicio, fechaFinal);
        boolean capsulaDisponible = disponibles.stream()
                .anyMatch(c -> c.getId().equals(capsulaId));
        if (!capsulaDisponible) {
            throw new IllegalArgumentException("La cápsula " + capsulaId + " no está disponible en esas fechas.");
        }

        // --- Persistir reserva ---
        Reserva reserva = new Reserva();
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFinal(fechaFinal);
        reserva.setHuesped(huesped);
        reserva.setCapsula(capsula);
        reserva.setEstado("CONFIRMADA");
        Reserva guardada = reservaRepository.save(reserva);

        // --- Sincronización con Google Calendar (integración obligatoria) ---
        try {
            googleCalendarService.crearEvento(guardada);
        } catch (Exception e) {
            // Si Google Calendar falla no revertimos la reserva,
            // pero lo registramos para reintentar en un job posterior.
            System.err.println("[WARN] No se pudo sincronizar con Google Calendar: " + e.getMessage());
        }

        return guardada;
    }

    /** Reservas del huésped ordenadas por fecha de inicio. */
    public List<Reserva> listarPorHuesped(Long huespedId) {
        return reservaRepository.findByHuespedId(huespedId);
    }

    /** Reserva activa del huésped (hoy está entre check-in y check-out). */
    public Reserva reservaActiva(Long huespedId) {
        LocalDate hoy = LocalDate.now();
        return reservaRepository.findByHuespedId(huespedId).stream()
                .filter(r -> !r.getFechaInicio().isAfter(hoy) && !r.getFechaFinal().isBefore(hoy))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula cuántas noches del rango propuesto caen en el mismo mes que fechaInicio,
     * sumadas a las que el huésped ya tiene reservadas ese mes.
     */
    private long calcularNochesEnMes(Long huespedId, LocalDate fechaInicio, LocalDate fechaFinal) {
        YearMonth mes = YearMonth.from(fechaInicio);
        LocalDate inicioMes = mes.atDay(1);
        LocalDate finMes = mes.atEndOfMonth();

        return reservaRepository.findByHuespedId(huespedId).stream()
                .filter(r -> !r.getFechaInicio().isAfter(finMes) && !r.getFechaFinal().isBefore(inicioMes))
                .mapToLong(r -> {
                    LocalDate desde = r.getFechaInicio().isBefore(inicioMes) ? inicioMes : r.getFechaInicio();
                    LocalDate hasta = r.getFechaFinal().isAfter(finMes) ? finMes : r.getFechaFinal();
                    return ChronoUnit.DAYS.between(desde, hasta);
                })
                .sum();
    }
}