package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.ISizeService;
import com.thegroup.pf_sgr.model.Size;
import com.thegroup.pf_sgr.repository.SizeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SizeService implements ISizeService {

    private final SizeRepository sizeRepository;

    @Override
    public Page<Size> getAllSizes(int page, int size) {
        return sizeRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Size getSizeById(Integer sizeId) {
        return sizeRepository.findById(sizeId)
                .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada con el ID: " + sizeId));
    }

    @Override
    public Size saveSize(Size size) {
        return sizeRepository.save(size);
    }

    @Override
    public void deleteSize(Integer sizeId) {
        Size size = sizeRepository.findById(sizeId)
                .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada con el ID: " + sizeId));
        sizeRepository.delete(size);
    }
}