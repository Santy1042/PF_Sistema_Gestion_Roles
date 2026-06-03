package com.thegroup.pf_sgr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVariantResponse {
    private Integer id;
    private String size;
    private String color;
    private Integer stock;
    private Boolean isActive;
    private String imageUrl;
}
