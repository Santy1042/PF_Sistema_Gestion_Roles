package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Color;
import java.util.List;

public interface IColorService {
    List<Color> getAllColors();
    Color getColorById(Integer colorId);
    Color saveColor(Color color);
    void deleteColor(Integer colorId);
}