package com.thegroup.service;

import com.thegroup.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private List<User> users = new ArrayList<>();



public void addUser(User user) {
    users.add(user);
}

public List<User> getUsers() {
    return users;
}

public List<User> searchUsers(String keyword) {

    List<User> results = new ArrayList<>();

    for (User user : users) {

        if (user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
            user.getEmail().toLowerCase().contains(keyword.toLowerCase())) {

            results.add(user);
        }
    }

    return results;
}

public boolean updateUser(Long id, User updatedUser) {

    for (User user : users) {

        if (user.getId().equals(id)) {

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());

            return true;
        }
    }

    return false;
}

public boolean deleteUser(Long id, Long adminId) {

    if (id.equals(adminId)) {
        return false;
    }

    for (User user : users) {

        if (user.getId().equals(id)) {
            users.remove(user);
            return true;
        }
    }

    return false;
}

}