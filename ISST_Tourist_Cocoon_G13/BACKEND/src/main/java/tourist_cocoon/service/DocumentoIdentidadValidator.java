package tourist_cocoon.service;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class DocumentoIdentidadValidator {

    private static final String LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    public String normalize(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }

    public boolean isValidDniOrNie(String raw) {
        String doc = normalize(raw);
        if (doc == null) return false;

        if (doc.matches("^\\d{8}[A-Z]$")) {
            return isValidDni(doc);
        }

        if (doc.matches("^[XYZ]\\d{7}[A-Z]$")) {
            return isValidNie(doc);
        }

        return false;
    }

    private boolean isValidDni(String dni) {
        int number = Integer.parseInt(dni.substring(0, 8));
        char expected = LETTERS.charAt(number % 23);
        return dni.charAt(8) == expected;
    }

    private boolean isValidNie(String nie) {
        char first = nie.charAt(0);
        String prefix;

        switch (first) {
            case 'X':
                prefix = "0";
                break;
            case 'Y':
                prefix = "1";
                break;
            case 'Z':
                prefix = "2";
                break;
            default:
                return false;
        }

        int number = Integer.parseInt(prefix + nie.substring(1, 8));
        char expected = LETTERS.charAt(number % 23);
        return nie.charAt(8) == expected;
    }
}