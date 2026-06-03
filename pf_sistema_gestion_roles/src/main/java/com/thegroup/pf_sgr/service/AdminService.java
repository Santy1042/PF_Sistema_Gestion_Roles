package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.dto.RoleChangeRequest;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.dto.AdminUserUpdateRequest;
import com.thegroup.pf_sgr.model.Role;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import com.thegroup.pf_sgr.repository.SaleRepository;
import com.thegroup.pf_sgr.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SaleRepository saleRepository;

    private ProfileResponse mapToProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getIdUser())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole().name())
                .active(user.getIsActive())
                .build();
    }

    @Override
    public List<ProfileResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .toList();
    }

    @Override
    public List<ProfileResponse> searchUsersByName(String name) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
            .stream()
            .map(this::mapToProfileResponse)
            .toList();
    }

    @Override
    public List<ProfileResponse> searchUsersByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email)
            .stream()
            .map(this::mapToProfileResponse)
            .toList();
    }

    @Override
    public ProfileResponse changeRole(Long id, RoleChangeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String requestedRole = request.getRole();
        validateRole(requestedRole);

        user.setRole(Role.valueOf(requestedRole.toUpperCase()));
        return mapToProfileResponse(userRepository.save(user));
    }

    @Override
    public ProfileResponse updateUser(Long id, AdminUserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateRole(request.getRole());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setIsActive(request.getIsActive());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return mapToProfileResponse(userRepository.save(user));
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

    @Override
    public DashboardStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalOrders = saleRepository.count();

        BigDecimal totalSalesAmount = saleRepository.sumTotalByStatuses(List.of("PAID", "PENDING"));
        if (totalSalesAmount == null) totalSalesAmount = BigDecimal.ZERO;

        return DashboardStatsResponse.builder()
            .totalUsers(totalUsers)
            .totalOrders(totalOrders)
            .totalSalesAmount(totalSalesAmount)
            .build();
    }
}
