package com.thegroup.pf_sgr.interfaces;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String name;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
