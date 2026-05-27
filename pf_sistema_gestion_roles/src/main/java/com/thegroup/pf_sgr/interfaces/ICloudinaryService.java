package com.thegroup.pf_sgr.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    String uploadImage(MultipartFile file);
}
