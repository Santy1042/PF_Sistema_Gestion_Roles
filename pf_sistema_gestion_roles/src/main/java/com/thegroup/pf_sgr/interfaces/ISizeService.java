package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Size;
import java.util.List;
import java.util.Optional;

public interface ISizeService {
    List<Size> getAllSizes();
    Optional<Size> getSizeById(Integer id);
    Size saveSize(Size size);
    boolean deleteSize(Integer id);
}