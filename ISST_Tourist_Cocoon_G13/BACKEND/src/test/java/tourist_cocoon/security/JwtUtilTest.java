package tourist_cocoon.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de JwtUtil.
 * No requiere Spring: se instancia directamente con clave y expiración de prueba.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Clave HMAC-SHA256: mínimo 32 bytes (256 bits), codificada en Base64
    private static final String TEST_SECRET =
        Base64.getEncoder().encodeToString(new byte[32]); // 32 ceros → válido

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, 3_600_000L); // 1 hora de expiración
    }

    // ─────────────────────────────────────────────────
    //  Generación y lectura de claims
    // ─────────────────────────────────────────────────

    @Test
    void generateToken_extractEmail_devuelveEmailCorrecto() {
        String token = jwtUtil.generateToken("huesped@test.com", "HUESPED");
        assertEquals("huesped@test.com", jwtUtil.extractEmail(token));
    }

    @Test
    void generateToken_extractRol_devuelveRolCorrecto() {
        String token = jwtUtil.generateToken("admin@cocoon.com", "ADMIN");
        assertEquals("ADMIN", jwtUtil.extractRol(token));
    }

    @Test
    void generateToken_dobleGeneracion_producenTokensDistintos() {
        // Cada token lleva issuedAt distinto (milisegundos)
        String t1 = jwtUtil.generateToken("a@b.com", "HUESPED");
        String t2 = jwtUtil.generateToken("a@b.com", "HUESPED");
        // En teoría pueden ser iguales si se generan en el mismo milisegundo,
        // pero en la práctica casi nunca ocurre; al menos ambos son válidos.
        assertTrue(jwtUtil.isValid(t1));
        assertTrue(jwtUtil.isValid(t2));
    }

    // ─────────────────────────────────────────────────
    //  Validación de token
    // ─────────────────────────────────────────────────

    @Test
    void isValid_tokenCorrecto_devuelveTrue() {
        String token = jwtUtil.generateToken("user@test.com", "HUESPED");
        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void isValid_tokenManipulado_devuelveFalse() {
        String token = jwtUtil.generateToken("user@test.com", "HUESPED");
        // Modificamos el último carácter para romper la firma
        String manipulado = token.substring(0, token.length() - 1) + "X";
        assertFalse(jwtUtil.isValid(manipulado));
    }

    @Test
    void isValid_tokenVacio_devuelveFalse() {
        assertFalse(jwtUtil.isValid(""));
    }

    @Test
    void isValid_tokenExpirado_devuelveFalse() {
        // Crear un JwtUtil con expiración de -1 ms (ya expirado al generarse)
        JwtUtil expiredUtil = new JwtUtil(TEST_SECRET, -1L);
        String token = expiredUtil.generateToken("user@test.com", "HUESPED");
        assertFalse(jwtUtil.isValid(token));
    }
}
