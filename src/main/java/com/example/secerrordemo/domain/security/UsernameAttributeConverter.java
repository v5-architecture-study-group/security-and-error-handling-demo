package com.example.secerrordemo.domain.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UsernameAttributeConverter implements AttributeConverter<Username, String> {

    @Override
    public String convertToDatabaseColumn(Username attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public Username convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Username.fromString(dbData);
    }
}
