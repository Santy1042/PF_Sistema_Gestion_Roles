package com.thegroup.pf_sgr.interfaces;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String logout();
}
