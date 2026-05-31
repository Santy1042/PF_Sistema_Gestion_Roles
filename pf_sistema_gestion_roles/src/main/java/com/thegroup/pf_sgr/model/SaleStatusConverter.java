package com.thegroup.pf_sgr.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SaleStatusConverter implements AttributeConverter<SaleStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SaleStatus status) {
        if (status == null) return null;
        switch (status) {
            case PENDING: return 1;
            case PAID: return 2;
            case CANCELLED: return 3;
            case REFUNDED: return 4;
            default: return 1;
        }
    }

    @Override
    public SaleStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        switch (dbData) {
            case 1: return SaleStatus.PENDING;
            case 2: return SaleStatus.PAID;
            case 3: return SaleStatus.CANCELLED;
            case 4: return SaleStatus.REFUNDED;
            default: return SaleStatus.PENDING;
        }
    }
}
