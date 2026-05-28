package com.thegroup.pf_sgr.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private BigDecimal price;
    private Boolean isActive = true;
    private Boolean isFeatured = false;
    private Integer discountPercentage = 0;
    private Integer categoryId; 
}