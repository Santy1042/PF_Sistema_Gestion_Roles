package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.User;

public interface IUserRepository {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}

