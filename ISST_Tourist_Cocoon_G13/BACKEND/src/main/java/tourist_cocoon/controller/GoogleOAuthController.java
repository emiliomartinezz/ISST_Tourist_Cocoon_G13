package tourist_cocoon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import tourist_cocoon.dto.GoogleOAuthStartResponseDTO;
import tourist_cocoon.dto.GoogleOAuthStatusDTO;
import tourist_cocoon.service.GoogleOAuthService;
import tourist_cocoon.repository.GoogleOAuthTokenRepository;

/**
 * Endpoints OAuth para que el cliente conecte su Google Calendar.
 */
@RestController
@RequestMapping("/google/oauth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class GoogleOAuthController {

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    private final GoogleOAuthService googleOAuthService;
    private final GoogleOAuthTokenRepository tokenRepository;

    public GoogleOAuthController(GoogleOAuthService googleOAuthService, GoogleOAuthTokenRepository tokenRepository) {
        this.googleOAuthService = googleOAuthService;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<GoogleOAuthStartResponseDTO> start(@RequestParam Long userId) {
        String authUrl = googleOAuthService.buildAuthUrl(userId).authUrl();
        return ResponseEntity.ok(new GoogleOAuthStartResponseDTO(authUrl));
    }

    /**
     * Callback OAuth (redirect_uri). Tras procesar, redirige al frontend.
     */
    @GetMapping("/callback")
    public RedirectView callback(@RequestParam String code, @RequestParam String state) {
        googleOAuthService.handleCallback(code, state);
        return new RedirectView(frontendBaseUrl + "/#/mi-perfil?google=connected");
    }

    @GetMapping("/status")
    public ResponseEntity<GoogleOAuthStatusDTO> status(@RequestParam Long userId) {
        return tokenRepository.findByUsuarioId(userId)
                .filter(t -> t.getRevokedAt() == null)
                .map(t -> ResponseEntity.ok(new GoogleOAuthStatusDTO(true, t.getCalendarId(), t.getConnectedAt())))
                .orElseGet(() -> ResponseEntity.ok(new GoogleOAuthStatusDTO(false, null, null)));
    }

    @DeleteMapping
    public ResponseEntity<?> disconnect(@RequestParam Long userId) {
        tokenRepository.findByUsuarioId(userId).ifPresent(tokenRepository::delete);
        return ResponseEntity.ok().build();
    }
}

