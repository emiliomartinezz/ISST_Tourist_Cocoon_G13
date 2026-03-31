package tourist_cocoon.controller;

import tourist_cocoon.dto.LoginRequestDTO;
import tourist_cocoon.dto.LoginResponseDTO;
import tourist_cocoon.dto.RegisterRequestDTO;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /api/auth/register
     * Registra un nuevo huésped. La contraseña se cifra con BCrypt (RGPD).
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese email.");
        }
        if (usuarioRepository.findByNif(dto.getNif()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese NIF/DNI.");
        }

        Usuario usuario = new Usuario();
        usuario.setNif(dto.getNif().trim().toUpperCase());
        usuario.setNombre(dto.getNombre().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        usuario.setRol("HUESPED");

        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(
            saved.getId(), saved.getNombre(), saved.getEmail(), saved.getRol()));
    }

    /**
     * POST /api/auth/login
     * Autentica email + contraseña. Devuelve datos del usuario para la sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail().trim().toLowerCase())
            .orElse(null);

        if (usuario == null || !passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        return ResponseEntity.ok(new LoginResponseDTO(
            usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol()));
    }
}