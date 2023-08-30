package com.example.secerrordemo.domain.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IpAddressAttributeConverter implements AttributeConverter<IpAddress, String> {

    @Override
    public String convertToDatabaseColumn(IpAddress attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public IpAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : IpAddress.fromString(dbData);
    }
}
