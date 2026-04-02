package tourist_cocoon.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tourist_cocoon.dto.SolicitudAccesoRequestDTO;
import tourist_cocoon.dto.SolicitudAccesoResponseDTO;
import tourist_cocoon.service.AccesoService;

@RestController
@RequestMapping("/accesos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    @PostMapping("/solicitar")
    public ResponseEntity<SolicitudAccesoResponseDTO> solicitarAcceso(
            @Valid @RequestBody SolicitudAccesoRequestDTO dto
    ) {
        SolicitudAccesoResponseDTO response = accesoService.solicitarAcceso(dto);
        return ResponseEntity.ok(response);
    }
}