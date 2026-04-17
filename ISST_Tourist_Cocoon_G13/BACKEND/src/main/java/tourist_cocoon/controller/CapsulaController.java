package tourist_cocoon.controller;

import jakarta.validation.Valid;
import tourist_cocoon.dto.ActualizarEstadoCapsulaRequestDTO;
import tourist_cocoon.model.Capsula;
import tourist_cocoon.repository.CapsulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/capsulas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CapsulaController {

    @Autowired
    private CapsulaRepository capsulaRepository;

    /**
     * GET /api/capsulas
     * Devuelve todas las cápsulas con su estado actual.
     */
    @GetMapping
    public List<Capsula> listarTodas() {
        return capsulaRepository.findAll();
    }

    /**
     * GET /api/capsulas/disponibles?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
     * Devuelve cápsulas sin reservas que se solapen con el rango dado.
     */
    @GetMapping("/disponibles")
    public List<Capsula> listarDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return capsulaRepository.findDisponiblesBetween(fechaInicio, fechaFin);
    }

    /**
     * GET /api/capsulas/{id}
     * Devuelve una cápsula por su ID (ej. "C-001").
     */
    @GetMapping("/{id}")
    public ResponseEntity<Capsula> obtener(@PathVariable String id) {
        return capsulaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/capsulas/{id}/estado
     * Actualiza el estado de una cápsula (Disponible / Ocupada / Sucia).
     * Usado por el administrador o por el proceso de check-in/checkout.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable String id,
            @Valid @RequestBody ActualizarEstadoCapsulaRequestDTO dto) {
        return capsulaRepository.findById(id)
                .map(capsula -> {
                    capsula.setEstado(dto.getEstado());
                    capsulaRepository.save(capsula);
                    return ResponseEntity.ok(capsula);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
