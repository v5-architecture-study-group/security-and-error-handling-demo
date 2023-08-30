package com.example.secerrordemo.domain.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SessionIdAttributeConverter implements AttributeConverter<SessionId, String> {

    @Override
    public String convertToDatabaseColumn(SessionId attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public SessionId convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SessionId.fromString(dbData);
    }
}
