package tourist_cocoon.controller;

import tourist_cocoon.dto.ReservaRequestDTO;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    /**
     * POST /api/reservas
     * Crea una reserva nueva. Valida reglas legales, disponibilidad
     * y sincroniza con Google Calendar.
     */
    @PostMapping
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReservaRequestDTO dto) {
        try {
            Reserva nueva = reservaService.crearReserva(
                dto.getHuespedId(),
                dto.getCapsulaId(),
                dto.getFechaInicio(),
                dto.getFechaFinal()
            );
            return ResponseEntity.ok(nueva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/reservas/huesped/{id}
     * Devuelve todas las reservas de un huésped ordenadas por fecha.
     */
    @GetMapping("/huesped/{id}")
    public List<Reserva> obtenerReservasPorHuesped(@PathVariable Long id) {
        return reservaService.listarPorHuesped(id);
    }

    /**
     * GET /api/reservas/huesped/{id}/activa
     * Devuelve la reserva activa del huésped (si existe).
     */
    @GetMapping("/huesped/{id}/activa")
    public ResponseEntity<?> reservaActiva(@PathVariable Long id) {
        Reserva activa = reservaService.reservaActiva(id);
        if (activa == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(activa);
    }
}