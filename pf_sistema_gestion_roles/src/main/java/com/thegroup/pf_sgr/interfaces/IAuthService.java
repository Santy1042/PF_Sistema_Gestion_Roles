package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.AuthResponse;
import com.thegroup.pf_sgr.dto.LoginRequest;
import com.thegroup.pf_sgr.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

}
