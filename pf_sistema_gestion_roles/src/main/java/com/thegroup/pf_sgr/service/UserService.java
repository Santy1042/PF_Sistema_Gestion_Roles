package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IUserService;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(user.getIdUser());
    }
}
