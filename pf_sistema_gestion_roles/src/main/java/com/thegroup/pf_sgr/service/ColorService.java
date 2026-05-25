package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.IColorService;
import com.thegroup.pf_sgr.model.Color;
import com.thegroup.pf_sgr.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService implements IColorService {

    private final ColorRepository colorRepository;

    @Override
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    @Override
    public Optional<Color> getColorById(Integer id) {
        return colorRepository.findById(id);
    }

    @Override
    public Color saveColor(Color color) {
        return colorRepository.save(color);
    }

    @Override
    public boolean deleteColor(Integer id) {
        if (colorRepository.existsById(id)) {
            colorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}