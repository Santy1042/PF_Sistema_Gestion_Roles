package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.IPerfilService;
import com.thegroup.pf_sgr.interfaces.PerfilRequest;
import com.thegroup.pf_sgr.interfaces.PerfilResponse;
import com.thegroup.pf_sgr.model.Usuario;
import com.thegroup.pf_sgr.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements IPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PerfilResponse getPerfil(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return PerfilResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }

    @Override
    public PerfilResponse updatePerfil(String correo, PerfilRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            usuario.setNombre(request.getNombre());
        }

        actualizarContrasenaIfPresent(usuario, request.getContrasena());

        usuario = usuarioRepository.save(usuario);

        return PerfilResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }

    private void actualizarContrasenaIfPresent(Usuario usuario, String nuevaContrasena) {
        if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        }
    }
}
