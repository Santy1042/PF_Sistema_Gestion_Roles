package com.thegroup.pf_sgr.util;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import org.springframework.security.core.Authentication;

public final class AuthUtils {

    private AuthUtils() {  }

    public static Long getUserId(Authentication authentication, UserRepository userRepository) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
        return user.getIdUser();
    }
}
