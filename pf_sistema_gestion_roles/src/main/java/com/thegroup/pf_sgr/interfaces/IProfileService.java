package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.ProfileRequest;
import com.thegroup.pf_sgr.dto.ProfileResponse;

public interface IProfileService {
    ProfileResponse getProfile(String email);
    ProfileResponse updateProfile(String email, ProfileRequest request);

}
