package tourist_cocoon.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tourist_cocoon.config.EncryptionUtil;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return EncryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            // Dato legacy en texto plano (anterior al cifrado) — se devuelve tal cual
            return dbData;
        }
    }
}
