package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private Rol rol;
    private String nombre;
    private String correo;
}
