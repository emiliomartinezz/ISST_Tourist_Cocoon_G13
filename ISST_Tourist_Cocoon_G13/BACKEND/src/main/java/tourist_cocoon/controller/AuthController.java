package tourist_cocoon.controller;

import tourist_cocoon.dto.LoginRequestDTO;
import tourist_cocoon.dto.LoginResponseDTO;
import tourist_cocoon.dto.RegisterRequestDTO;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.UsuarioRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        String email = normalizeEmail(dto.getEmail());
        String nif = normalizeUpper(dto.getNif());
        String nombre = normalizeText(dto.getNombre());
        String telefono = normalizeOptionalText(dto.getTelefono());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ErrorResponseException(
                    HttpStatus.CONFLICT,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT,
                            "Ya existe un usuario con ese email"
                    ),
                    null
            );
        }

        if (usuarioRepository.findByNif(nif).isPresent()) {
            throw new ErrorResponseException(
                    HttpStatus.CONFLICT,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT,
                            "Ya existe un usuario con ese NIF/DNI"
                    ),
                    null
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNif(nif);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setTelefono(telefono);
        usuario.setRol("HUESPED");

        Usuario saved = usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponseDTO(
                        saved.getId(),
                        saved.getNombre(),
                        saved.getEmail(),
                        saved.getRol()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        String email = normalizeEmail(dto.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorResponseException(
                        HttpStatus.UNAUTHORIZED,
                        org.springframework.http.ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNAUTHORIZED,
                                "Credenciales incorrectas"
                        ),
                        null
                ));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new ErrorResponseException(
                    HttpStatus.UNAUTHORIZED,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.UNAUTHORIZED,
                            "Credenciales incorrectas"
                    ),
                    null
            );
        }

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getRol()
                )
        );
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeUpper(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}