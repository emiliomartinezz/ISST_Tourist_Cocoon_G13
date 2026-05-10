package tourist_cocoon.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de EncryptionUtil (cifrado AES-128 ECB).
 * No requiere contexto Spring: se inicializa la clave mediante el setter directamente.
 */
class EncryptionUtilTest {

    @BeforeAll
    static void inicializarClave() {
        // El setter con @Value establece el campo estático; lo llamamos manualmente en tests
        new EncryptionUtil().setEncryptionKey("TouristCocoonKey"); // 16 chars = 128 bits
    }

    // ─────────────────────────────────────────────────
    //  Cifrado / descifrado
    // ─────────────────────────────────────────────────

    @Test
    void encrypt_luego_decrypt_devuelveValorOriginal() {
        String original = "12345678Z";
        String cifrado  = EncryptionUtil.encrypt(original);
        String resultado = EncryptionUtil.decrypt(cifrado);
        assertEquals(original, resultado);
    }

    @Test
    void encrypt_mismoValor_produceSiempreElMismoCifrado() {
        // AES/ECB es determinista: misma clave + mismo texto → mismo cifrado
        String a = EncryptionUtil.encrypt("12345678Z");
        String b = EncryptionUtil.encrypt("12345678Z");
        assertEquals(a, b);
    }

    @Test
    void encrypt_valorNull_devuelveNull() {
        assertNull(EncryptionUtil.encrypt(null));
    }

    @Test
    void decrypt_valorNull_devuelveNull() {
        assertNull(EncryptionUtil.decrypt(null));
    }

    @Test
    void encrypt_valorCifradoEsDistintoAlOriginal() {
        String original = "00000000T";
        String cifrado  = EncryptionUtil.encrypt(original);
        assertNotEquals(original, cifrado);
    }

    @Test
    void encrypt_dosNifsDistintos_producenCifradosDistintos() {
        String a = EncryptionUtil.encrypt("12345678Z");
        String b = EncryptionUtil.encrypt("00000000T");
        assertNotEquals(a, b);
    }
}
