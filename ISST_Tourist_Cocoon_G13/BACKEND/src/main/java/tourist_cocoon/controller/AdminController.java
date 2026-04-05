package tourist_cocoon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tourist_cocoon.dto.RegistroAccesoAdminDTO;
import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.OrdenLimpieza;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.OrdenLimpiezaRepository;
import tourist_cocoon.repository.ReservaRepository;
import tourist_cocoon.repository.UsuarioRepository;
import tourist_cocoon.service.AccesoService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminController {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CapsulaRepository capsulaRepository;
    @Autowired private OrdenLimpiezaRepository ordenLimpiezaRepository;
    @Autowired private AccesoService accesoService;

    @GetMapping("/reservas")
    public List<Reserva> listarTodasReservas() {
        return reservaRepository.findAll();
    }

    @GetMapping("/usuarios")
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/capsulas")
    public List<Capsula> listarTodasCapsulas() {
        return capsulaRepository.findAll();
    }

    @GetMapping("/ordenes-limpieza")
    public List<OrdenLimpieza> listarOrdenesLimpieza() {
        return ordenLimpiezaRepository.findAll();
    }

    @GetMapping("/accesos")
    public List<RegistroAccesoAdminDTO> listarRegistrosAcceso() {
        return accesoService.listarTodosLosRegistros();
    }

    @PatchMapping("/capsulas/{id}/estado")
    public ResponseEntity<?> actualizarEstadoCapsula(
            @PathVariable String id,
            @RequestParam String estado) {

        List<String> estadosValidos = List.of("Disponible", "Ocupada", "Sucia");
        if (!estadosValidos.contains(estado)) {
            return ResponseEntity.badRequest()
                    .body("Estado no válido. Valores permitidos: " + estadosValidos);
        }

        return capsulaRepository.findById(id).map(capsula -> {
            capsula.setEstado(estado);
            capsulaRepository.save(capsula);
            return ResponseEntity.ok(capsula);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/ordenes-limpieza/{id}/completar")
    public ResponseEntity<?> completarOrdenLimpieza(@PathVariable Long id) {
        return ordenLimpiezaRepository.findById(id).map(orden -> {
            orden.setEstado("COMPLETADA");

            Capsula capsula = orden.getCapsula();
            capsula.setEstado("Disponible");
            capsulaRepository.save(capsula);

            ordenLimpiezaRepository.save(orden);
            return ResponseEntity.ok(orden);
        }).orElse(ResponseEntity.notFound().build());
    }
}
