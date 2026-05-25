package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Color;
import java.util.List;
import java.util.Optional;

public interface IColorService {
    List<Color> getAllColors();
    Optional<Color> getColorById(Integer id);
    Color saveColor(Color color);
    boolean deleteColor(Integer id);
}