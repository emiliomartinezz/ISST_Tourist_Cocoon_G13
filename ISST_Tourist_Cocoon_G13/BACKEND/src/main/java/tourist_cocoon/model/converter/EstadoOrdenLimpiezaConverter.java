package tourist_cocoon.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tourist_cocoon.model.enums.EstadoOrdenLimpieza;

@Converter(autoApply = false)
public class EstadoOrdenLimpiezaConverter implements AttributeConverter<EstadoOrdenLimpieza, String> {

    @Override
    public String convertToDatabaseColumn(EstadoOrdenLimpieza attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public EstadoOrdenLimpieza convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        String normalized = dbData.trim().toUpperCase();

        return switch (normalized) {
            case "PENDIENTE" -> EstadoOrdenLimpieza.PENDIENTE;
            case "COMPLETADA" -> EstadoOrdenLimpieza.COMPLETADA;
            default -> throw new IllegalArgumentException("EstadoOrdenLimpieza no válido en BD: " + dbData);
        };
    }
}