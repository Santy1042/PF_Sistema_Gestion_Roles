package com.thegroup.pf_sgr.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        if (role == null) return null;
        if (role == Role.ADMIN) return 1;
        if (role == Role.USER) return 2;
        return 2;
    }

    @Override
    public Role convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        if (dbData == 1) return Role.ADMIN;
        if (dbData == 2) return Role.USER;
        return Role.USER;
    }
}
