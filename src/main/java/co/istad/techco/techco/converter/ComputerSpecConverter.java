package co.istad.techco.techco.converter;

import co.istad.techco.techco.domain.ComputerSpec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ComputerSpecConverter implements AttributeConverter<ComputerSpec, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ComputerSpec computerSpec) {
        if (computerSpec == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(computerSpec);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ComputerSpec to JSON", e);
        }
    }

    @Override
    public ComputerSpec convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ComputerSpec.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to ComputerSpec", e);
        }
    }
}

