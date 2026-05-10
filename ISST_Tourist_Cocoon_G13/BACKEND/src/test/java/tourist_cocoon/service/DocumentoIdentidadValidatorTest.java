package tourist_cocoon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del validador de DNI/NIE español.
 * No requiere Spring: instancia directa del componente.
 */
class DocumentoIdentidadValidatorTest {

    private DocumentoIdentidadValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DocumentoIdentidadValidator();
    }

    // ─────────────────────────────────────────────────
    //  normalize()
    // ─────────────────────────────────────────────────

    @Test
    void normalize_eliminaEspaciosYConvierteAMayusculas() {
        assertEquals("12345678Z", validator.normalize(" 12345678z "));
    }

    @Test
    void normalize_eliminaGuiones() {
        assertEquals("12345678Z", validator.normalize("12345678-Z"));
    }

    @Test
    void normalize_null_devuelveNull() {
        assertNull(validator.normalize(null));
    }

    // ─────────────────────────────────────────────────
    //  DNI válidos e inválidos
    // ─────────────────────────────────────────────────

    @Test
    void isValidDniOrNie_dniValido_devuelveTrue() {
        // 12345678 % 23 = 14 → letra 'Z'
        assertTrue(validator.isValidDniOrNie("12345678Z"));
    }

    @Test
    void isValidDniOrNie_dniLetraIncorrecta_devuelveFalse() {
        assertFalse(validator.isValidDniOrNie("12345678A")); // la letra correcta es Z
    }

    @Test
    void isValidDniOrNie_dniConFormatoMinusculas_devuelveTrue() {
        // normalize() convierte a mayúsculas antes de validar
        assertTrue(validator.isValidDniOrNie("12345678z"));
    }

    @Test
    void isValidDniOrNie_dniFormatoInvalido_devuelveFalse() {
        assertFalse(validator.isValidDniOrNie("1234567")); // solo 7 dígitos
    }

    // ─────────────────────────────────────────────────
    //  NIE válidos e inválidos
    // ─────────────────────────────────────────────────

    @Test
    void isValidDniOrNie_nieConPrefixX_valido() {
        // X1234567 → 01234567 → 1234567 % 23 = 19 → letra 'L'
        assertTrue(validator.isValidDniOrNie("X1234567L"));
    }

    @Test
    void isValidDniOrNie_nieConPrefixY_valido() {
        // Y → prefix "1" → número 10000000 → 10000000 % 23 = 14 → LETTERS[14] = 'Z'
        assertTrue(validator.isValidDniOrNie("Y0000000Z"));
    }

    @Test
    void isValidDniOrNie_nieLetraIncorrecta_devuelveFalse() {
        assertFalse(validator.isValidDniOrNie("X1234567A")); // la letra correcta es L
    }

    @Test
    void isValidDniOrNie_cadenaVacia_devuelveFalse() {
        assertFalse(validator.isValidDniOrNie(""));
    }

    @Test
    void isValidDniOrNie_null_devuelveFalse() {
        // normalize() devuelve null, y null.matches() lanzaría NPE sin la guarda
        assertFalse(validator.isValidDniOrNie(null));
    }
}
