package com.touristcocoon.controller;

import com.touristcocoon.model.Reserva;
import com.touristcocoon.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:3000") // Permite peticiones desde el frontend React
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody Reserva reserva) {
        // 1. Validar reglas de dominio (estancia máxima para evitar vivienda permanente)
        if (!reservaService.validarReglasEstancia(reserva)) {
            return ResponseEntity.badRequest()
                .body("La reserva excede el límite de noches permitido por la normativa.");
        }

        // 2. Aquí se llamaría a la integración obligatoria con Google Calendar
        // (La lógica se implementaría en el Service según el SDD)
        
        // 3. Guardar en la base de datos relacional
        Reserva nuevaReserva = reservaService.guardarReserva(reserva);
        return ResponseEntity.ok(nuevaReserva);
    }

    @GetMapping("/huesped/{id}")
    public List<Reserva> obtenerReservasPorHuesped(@PathVariable Long id) {
        return reservaService.listarPorHuesped(id);
    }
}