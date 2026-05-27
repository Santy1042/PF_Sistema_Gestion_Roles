package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
    private String name;
    private String email;
}
