package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.interfaces.PerfilResponse;
import com.thegroup.pf_sgr.model.Rol;
import com.thegroup.pf_sgr.model.Usuario;
import com.thegroup.pf_sgr.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<PerfilResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> PerfilResponse.builder()
                        .id(usuario.getId())
                        .nombre(usuario.getNombre())
                        .correo(usuario.getCorreo())
                        .rol(usuario.getRol())
                        .build())
                .toList();
    }

    @Override
    public PerfilResponse cambiarRol(Long id, Map<String, String> body) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String nuevoRol = body.get("rol");
        validarRol(nuevoRol);

        usuario.setRol(Rol.valueOf(nuevoRol));
        usuario = usuarioRepository.save(usuario);

        return PerfilResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }

    @Override
    public String eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuarioRepository.deleteById(id);
        return "Usuario " + usuario.getNombre() + " eliminado exitosamente";
    }

    private void validarRol(String rol) {
        try {
            Rol.valueOf(rol);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + rol);
        }
    }
}
