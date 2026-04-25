package tourist_cocoon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tourist_cocoon.dto.CancelarReservaRequestDTO;
import tourist_cocoon.dto.CheckoutRequestDTO;
import tourist_cocoon.dto.ReservaRequestDTO;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.service.ReservaService;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReservaRequestDTO dto) {
        Reserva reserva = reservaService.crearReserva(
            dto.getHuespedId(),
            dto.getCapsulaId(),
            dto.getFechaInicio(),
            dto.getFechaFinal(),
            dto.getStripePaymentIntentId()
        );
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarReserva(@Valid @RequestBody ReservaRequestDTO dto) {
        reservaService.validarReserva(
            dto.getHuespedId(),
            dto.getCapsulaId(),
            dto.getFechaInicio(),
            dto.getFechaFinal()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<Reserva>> listarPorHuesped(@PathVariable Long huespedId) {
        return ResponseEntity.ok(reservaService.listarPorHuesped(huespedId));
    }

    @GetMapping("/huesped/{huespedId}/activa")
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
            @Valid @RequestBody CheckoutRequestDTO dto
    ) {
        reservaService.procesarCheckOut(id, dto.getHuespedId(), dto.getFechaSalida());
        return ResponseEntity.ok("Checkout realizado correctamente");
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
        @PathVariable Long id,
        @Valid @RequestBody CancelarReservaRequestDTO dto
    ) {
        reservaService.cancelarReserva(id, dto.getHuespedId());
        return ResponseEntity.ok("Reserva cancelada correctamente");
    }
}