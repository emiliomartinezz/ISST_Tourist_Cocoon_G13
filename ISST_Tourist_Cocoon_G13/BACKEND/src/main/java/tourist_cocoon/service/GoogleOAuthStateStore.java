package tourist_cocoon.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Almacén simple de 'state' OAuth para el MVP.
 * Evita CSRF asociando un state aleatorio con un userId, con caducidad.
 */
@Component
public class GoogleOAuthStateStore {

    private static final Duration TTL = Duration.ofMinutes(10);

    private static class Entry {
        final Long userId;
        final Instant expiresAt;

        Entry(Long userId, Instant expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }

    private final ConcurrentHashMap<String, Entry> states = new ConcurrentHashMap<>();

    public String createState(Long userId) {
        String state = UUID.randomUUID().toString();
        states.put(state, new Entry(userId, Instant.now().plus(TTL)));
        return state;
    }

    /**
     * Consume el state (one-time) y devuelve el userId asociado.
     */
    public Optional<Long> consumeState(String state) {
        if (state == null || state.isBlank()) {
            return Optional.empty();
        }
        Entry entry = states.remove(state);
        if (entry == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            return Optional.empty();
        }
        return Optional.of(entry.userId);
    }
}

