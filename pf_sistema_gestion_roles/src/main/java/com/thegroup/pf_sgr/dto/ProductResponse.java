package com.thegroup.pf_sgr.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {
    private Integer productId;
    private String name;
    private BigDecimal price;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer discountPercentage;
}