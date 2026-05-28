package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Color;

public interface IColorService {
    Page<Color> getAllColors(int page, int size);
    Color getColorById(Integer colorId);
    Color saveColor(Color color);
    void deleteColor(Integer colorId);
}