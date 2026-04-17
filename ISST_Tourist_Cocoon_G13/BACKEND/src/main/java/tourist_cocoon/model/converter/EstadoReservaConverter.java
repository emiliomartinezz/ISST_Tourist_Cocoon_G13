package tourist_cocoon.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tourist_cocoon.model.enums.EstadoReserva;

@Converter(autoApply = false)
public class EstadoReservaConverter implements AttributeConverter<EstadoReserva, String> {

    @Override
    public String convertToDatabaseColumn(EstadoReserva attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public EstadoReserva convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        String normalized = dbData.trim().toUpperCase();

        return switch (normalized) {
            case "CONFIRMADA", "ACTIVA" -> EstadoReserva.CONFIRMADA;
            case "CANCELADA" -> EstadoReserva.CANCELADA;
            case "FINALIZADA" -> EstadoReserva.FINALIZADA;
            default -> throw new IllegalArgumentException("EstadoReserva no válido en BD: " + dbData);
        };
    }
}