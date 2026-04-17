package tourist_cocoon.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tourist_cocoon.dto.CrearIncidenciaRequestDTO;
import tourist_cocoon.dto.IncidenciaResponseDTO;
import tourist_cocoon.service.IncidenciaService;

@RestController
@RequestMapping("/incidencias")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;

    @PostMapping
    public ResponseEntity<IncidenciaResponseDTO> crear(@Valid @RequestBody CrearIncidenciaRequestDTO dto) {
        return ResponseEntity.ok(incidenciaService.reportar(dto));
    }

    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<IncidenciaResponseDTO>> listarPorHuesped(@PathVariable Long huespedId) {
        return ResponseEntity.ok(incidenciaService.listarPorHuesped(huespedId));
    }

    @GetMapping("/huesped/{huespedId}/abiertas")
    public ResponseEntity<List<IncidenciaResponseDTO>> listarAbiertasPorHuesped(@PathVariable Long huespedId) {
        return ResponseEntity.ok(incidenciaService.listarAbiertasPorHuesped(huespedId));
    }
}
