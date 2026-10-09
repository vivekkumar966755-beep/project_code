package com.example.demo.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = false)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? Role.PARTICIPANT.name() : role.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Role.PARTICIPANT;
        }
        String normalized = dbData.replace("ROLE_", "").trim().toUpperCase(Locale.ROOT);
        if ("USER".equals(normalized)) {
            return Role.PARTICIPANT;
        }
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return Role.PARTICIPANT;
        }
    }
}
