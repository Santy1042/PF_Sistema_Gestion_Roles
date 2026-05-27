package com.thegroup.pf_sgr.interfaces;

public interface IProfileService {
    ProfileResponse getProfile(String email);
    ProfileResponse updateProfile(String email, ProfileRequest request);
}
