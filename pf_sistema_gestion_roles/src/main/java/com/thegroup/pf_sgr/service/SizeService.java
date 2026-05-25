package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.interfaces.ISizeService;
import com.thegroup.pf_sgr.model.Size;
import com.thegroup.pf_sgr.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SizeService implements ISizeService {

    private final SizeRepository sizeRepository;

    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    @Override
    public Optional<Size> getSizeById(Integer id) {
        return sizeRepository.findById(id);
    }

    @Override
    public Size saveSize(Size size) {
        return sizeRepository.save(size);
    }

    @Override
    public boolean deleteSize(Integer id) {
        if (sizeRepository.existsById(id)) {
            sizeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}