package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.ITagService;
import com.thegroup.pf_sgr.model.Tag;
import com.thegroup.pf_sgr.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Override
    public Page<Tag> getAllTags(int page, int size) {
        return tagRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Tag getTagById(Integer tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrada con el ID: " + tagId));
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Integer tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrada con el ID: " + tagId));
        tagRepository.delete(tag);
    }
}