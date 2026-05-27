package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.AuthResponse;
import com.thegroup.pf_sgr.interfaces.IAuthService;
import com.thegroup.pf_sgr.interfaces.LoginRequest;
import com.thegroup.pf_sgr.interfaces.RegisterRequest;
import com.thegroup.pf_sgr.model.Role;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        validateUniqueEmail(request.getEmail());

        User user = new User(
                null,
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        user = userRepository.save(user);
        String token = jwtProvider.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtProvider.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public String logout() {
        return "Logout exitoso";
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
    }
}
