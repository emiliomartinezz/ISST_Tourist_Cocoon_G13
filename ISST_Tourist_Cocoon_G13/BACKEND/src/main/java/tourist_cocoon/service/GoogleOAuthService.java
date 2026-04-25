package tourist_cocoon.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.http.HttpStatus;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.TokenResponse;

import tourist_cocoon.model.GoogleOAuthToken;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.GoogleOAuthTokenRepository;
import tourist_cocoon.repository.UsuarioRepository;

/**
 * Servicio OAuth para conectar el Google Calendar del cliente.
 *
 * Guarda refresh_token en BD para poder sincronizar reservas del usuario.
 */
@Service
public class GoogleOAuthService {

    public static final String SCOPE_EVENTS = "https://www.googleapis.com/auth/calendar.events";

    @Value("${google.oauth.client-id:#{null}}")
    private String clientId;

    @Value("${google.oauth.client-secret:#{null}}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri:#{null}}")
    private String redirectUri;

    private final GoogleOAuthStateStore stateStore;
    private final UsuarioRepository usuarioRepository;
    private final GoogleOAuthTokenRepository tokenRepository;

    public GoogleOAuthService(
            GoogleOAuthStateStore stateStore,
            UsuarioRepository usuarioRepository,
            GoogleOAuthTokenRepository tokenRepository
    ) {
        this.stateStore = stateStore;
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
    }

    public record StartResult(String authUrl) {}

    public record CallbackResult(Long userId, String refreshToken, OffsetDateTime connectedAt) {}

    public StartResult buildAuthUrl(Long userId) {
        ensureConfigured();

        String state = stateStore.createState(userId);
        GoogleAuthorizationCodeFlow flow = buildFlow();

        String url = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(state)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        return new StartResult(url);
    }

    public CallbackResult handleCallback(String code, String state) {
        ensureConfigured();

        Long userId = stateStore.consumeState(state)
                .orElseThrow(() -> new ErrorResponseException(
                        HttpStatus.BAD_REQUEST,
                        org.springframework.http.ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "State OAuth inválido o caducado. Vuelve a intentarlo."
                        ),
                        null
                ));

        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            TokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            String refreshToken = tokenResponse.getRefreshToken();
            OffsetDateTime now = OffsetDateTime.now();

            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new ErrorResponseException(
                            HttpStatus.NOT_FOUND,
                            org.springframework.http.ProblemDetail.forStatusAndDetail(
                                    HttpStatus.NOT_FOUND,
                                    "Usuario no encontrado"
                            ),
                            null
                    ));

            Optional<GoogleOAuthToken> existing = tokenRepository.findByUsuarioId(userId);
            GoogleOAuthToken token = existing.orElseGet(GoogleOAuthToken::new);
            token.setUsuario(usuario);

            // Si Google no devuelve refresh_token (p.ej. ya concedido antes), mantenemos el previo si existía.
            if (refreshToken != null && !refreshToken.isBlank()) {
                token.setRefreshToken(refreshToken);
            } else if (existing.isEmpty()) {
                throw new ErrorResponseException(
                        HttpStatus.BAD_REQUEST,
                        org.springframework.http.ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "Google no devolvió refresh token. Revoca el acceso en tu cuenta de Google y vuelve a conectar."
                        ),
                        null
                );
            }

            token.setRevokedAt(null);
            token.setConnectedAt(now);
            tokenRepository.save(token);

            return new CallbackResult(userId, token.getRefreshToken(), now);
        } catch (Exception e) {
            throw new ErrorResponseException(
                    HttpStatus.BAD_REQUEST,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            "No se pudo completar la conexión con Google. Inténtalo de nuevo."
                    ),
                    e
            );
        }
    }

    private void ensureConfigured() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank() ||
                redirectUri == null || redirectUri.isBlank()) {
            throw new ErrorResponseException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.SERVICE_UNAVAILABLE,
                            "Google OAuth no está configurado en el servidor."
                    ),
                    null
            );
        }
    }

    private GoogleAuthorizationCodeFlow buildFlow() {
        try {
            GoogleClientSecrets secrets = new GoogleClientSecrets()
                    .setInstalled(new GoogleClientSecrets.Details()
                            .setClientId(clientId)
                            .setClientSecret(clientSecret));

            return new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    secrets,
                    List.of(SCOPE_EVENTS)
            ).build();
        } catch (Exception e) {
            throw new ErrorResponseException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    org.springframework.http.ProblemDetail.forStatusAndDetail(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error interno preparando OAuth de Google."
                    ),
                    e
            );
        }
    }
}

