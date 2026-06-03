package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.dto.RoleChangeRequest;
import com.thegroup.pf_sgr.dto.AdminUserUpdateRequest;
import com.thegroup.pf_sgr.dto.DashboardStatsResponse;

import java.util.List;

public interface IAdminService {

    List<ProfileResponse> listUsers();
    ProfileResponse changeRole(Long id, RoleChangeRequest request);
    ProfileResponse updateUser(Long id, AdminUserUpdateRequest request);
    String deleteUser(Long id);
    List<ProfileResponse> searchUsersByName(String name);
    List<ProfileResponse> searchUsersByEmail(String email);
    DashboardStatsResponse getStats();
}
