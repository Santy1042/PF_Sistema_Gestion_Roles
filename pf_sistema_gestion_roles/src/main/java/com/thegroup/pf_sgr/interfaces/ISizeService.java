package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Size; 

public interface ISizeService {
    Page<Size> getAllSizes(int page, int size);
    Size getSizeById(Integer sizeId);
    Size saveSize(Size size);
    Size updateSize(Integer sizeId, Size size);
    void deleteSize(Integer sizeId);
}
