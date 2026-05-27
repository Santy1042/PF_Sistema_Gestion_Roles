package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.User;
import java.util.List;

public interface UserServiceInterface {

    List<User> getUsers();

    User addUser(User user);

    List<User> searchUsers(String keyword);

    User updateUser(Long id, User updatedUser);

    boolean deleteUser(Long id, Long adminId);
}