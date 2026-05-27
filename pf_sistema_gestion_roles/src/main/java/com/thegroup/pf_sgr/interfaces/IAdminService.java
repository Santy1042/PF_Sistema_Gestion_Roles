package com.thegroup.pf_sgr.interfaces;

import java.util.List;
import java.util.Map;

public interface IAdminService {
    List<ProfileResponse> listUsers();
    ProfileResponse changeRole(Long id, Map<String, String> body);
    String deleteUser(Long id);
}
