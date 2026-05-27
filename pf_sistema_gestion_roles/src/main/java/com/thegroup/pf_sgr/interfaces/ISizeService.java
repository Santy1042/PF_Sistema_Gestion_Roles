package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Size;
import java.util.List;

public interface ISizeService {
    List<Size> getAllSizes();
    Size getSizeById(Integer sizeId);
    Size saveSize(Size size);
    void deleteSize(Integer sizeId);
}