package tourist_cocoon.controller;

import tourist_cocoon.dto.CheckoutRequestDTO;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.service.ReservaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, String> body) {
        try {
            Long huespedId = Long.valueOf(body.get("huespedId"));
            String capsulaId = body.get("capsulaId");
            LocalDate fechaInicio = LocalDate.parse(body.get("fechaInicio"));
            LocalDate fechaFinal = LocalDate.parse(body.get("fechaFinal"));

            Reserva reserva = reservaService.crearReserva(huespedId, capsulaId, fechaInicio, fechaFinal);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear reserva: " + e.getMessage());
        }
    }

    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<Reserva>> listarPorHuesped(@PathVariable Long huespedId) {
        return ResponseEntity.ok(reservaService.listarPorHuesped(huespedId));
    }

    @GetMapping("/activa/{huespedId}")
    public ResponseEntity<?> obtenerReservaActiva(@PathVariable Long huespedId) {
        Reserva activa = reservaService.reservaActiva(huespedId);
        if (activa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activa);
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<?> realizarCheckOut(
            @PathVariable Long id,
            @RequestBody CheckoutRequestDTO dto
    ) {
        try {
            reservaService.procesarCheckOut(id, dto.getHuespedId(), dto.getFechaSalida());
            return ResponseEntity.ok("Checkout realizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al realizar checkout: " + e.getMessage());
        }
    }
}