package com.thegroup.pf_sgr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequest {
    
    @NotBlank(message = "El rol no puede estar vacío")
    private String role;
    
}