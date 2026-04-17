package tourist_cocoon.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tourist_cocoon.model.enums.EstadoCapsula;

@Converter(autoApply = false)
public class EstadoCapsulaConverter implements AttributeConverter<EstadoCapsula, String> {

    @Override
    public String convertToDatabaseColumn(EstadoCapsula attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public EstadoCapsula convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        String normalized = dbData.trim().toUpperCase();

        return switch (normalized) {
            case "DISPONIBLE" -> EstadoCapsula.DISPONIBLE;
            case "OCUPADA" -> EstadoCapsula.OCUPADA;
            case "SUCIA" -> EstadoCapsula.SUCIA;
            case "BLOQUEADA", "NO_DISPONIBLE" -> EstadoCapsula.BLOQUEADA;
            default -> throw new IllegalArgumentException("EstadoCapsula no válido en BD: " + dbData);
        };
    }
}