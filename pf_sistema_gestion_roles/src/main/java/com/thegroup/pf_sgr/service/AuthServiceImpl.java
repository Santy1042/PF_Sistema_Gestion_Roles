package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.AuthResponse;
import com.thegroup.pf_sgr.interfaces.IAuthService;
import com.thegroup.pf_sgr.interfaces.LoginRequest;
import com.thegroup.pf_sgr.interfaces.RegisterRequest;
import com.thegroup.pf_sgr.model.Rol;
import com.thegroup.pf_sgr.model.Usuario;
import com.thegroup.pf_sgr.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        validarCorreoUnico(request.getCorreo());
        
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(Rol.USUARIO)
                .build();

        usuario = usuarioRepository.save(usuario);
        String token = jwtProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .rol(usuario.getRol())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .rol(usuario.getRol())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .build();
    }

    @Override
    public String logout() {
        return "Logout exitoso";
    }

    private void validarCorreoUnico(String correo) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
    }
}
