package com.safewalk.model.enums.converter;

import com.safewalk.model.enums.OccurrenceEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OccurrenceEnumConverter implements AttributeConverter<OccurrenceEnum, String> {

    @Override
    public String convertToDatabaseColumn(OccurrenceEnum attribute) {
        if (attribute == null) return null;
        return attribute.getDescription();
    }

    @Override
    public OccurrenceEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return OccurrenceEnum.fromDescription(dbData);
    }
}