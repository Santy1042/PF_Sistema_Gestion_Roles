package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.dto.RoleChangeRequest;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.model.Role;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final UserRepository userRepository;

    @Override
    public List<ProfileResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> ProfileResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole().name())
                        .build())
                .toList();
    }

    @Override
    public ProfileResponse changeRole(Long id, RoleChangeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String requestedRole = request.getRole();
        validateRole(requestedRole);

        user.setRole(Role.valueOf(requestedRole.toUpperCase()));
        user = userRepository.save(user);

        return ProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(id);
        return "Usuario " + user.getFirstName() + " eliminado exitosamente";
    }

    private void validateRole(String requestedRole) {
        try {
            Role.valueOf(requestedRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + requestedRole);
        }
    }
}