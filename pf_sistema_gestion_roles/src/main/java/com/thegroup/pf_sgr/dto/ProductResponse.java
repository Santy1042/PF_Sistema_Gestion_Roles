package com.thegroup.pf_sgr.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Integer productId;
    private String name;
    private BigDecimal price;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer discountPercentage;
    private Integer categoryId;
    private String categoryName;
    private List<String> tags;
    private List<ProductVariantResponse> variants;
}
