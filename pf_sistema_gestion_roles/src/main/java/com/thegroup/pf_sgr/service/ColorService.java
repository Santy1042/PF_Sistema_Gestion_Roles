package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IColorService;
import com.thegroup.pf_sgr.model.Color;
import com.thegroup.pf_sgr.repository.ColorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ColorService implements IColorService {

    private final ColorRepository colorRepository;

    @Override
    public Page<Color> getAllColors(int page, int size) {
        return colorRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Color getColorById(Integer colorId) {
        return colorRepository.findById(colorId)
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con el ID: " + colorId));
    }

    @Override
    public Color saveColor(Color color) {
        return colorRepository.save(color);
    }

    @Override
    public void deleteColor(Integer colorId) {
        Color color = colorRepository.findById(colorId)
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con el ID: " + colorId));
        colorRepository.delete(color);
    }
}