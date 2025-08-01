package co.istad.techco.techco.converter;

import co.istad.techco.techco.domain.ColorOption;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
@Converter
public class JsonConverter implements AttributeConverter<List<ColorOption>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ColorOption> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // Return empty array if serialization fails
        }
    }

    @Override
    public List<ColorOption> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ColorOption>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list if deserialization fails
        }
    }
}

