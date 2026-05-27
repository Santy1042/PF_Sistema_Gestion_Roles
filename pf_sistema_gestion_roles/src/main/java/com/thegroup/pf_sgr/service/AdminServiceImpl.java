package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.interfaces.ProfileResponse;
import com.thegroup.pf_sgr.model.Role;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final UserRepository userRepository;

    @Override
    public List<ProfileResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> new ProfileResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

    @Override
    public ProfileResponse changeRole(Long id, Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String requestedRole = body.get("rol");
        validateRole(requestedRole);

        user.setRole(Role.valueOf(requestedRole));
        user = userRepository.save(user);

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

    @Override
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(id);
        return "Usuario " + user.getName() + " eliminado exitosamente";
    }

    private void validateRole(String requestedRole) {
        try {
            Role.valueOf(requestedRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + requestedRole);
        }
    }
}
