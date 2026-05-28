package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Tag;

public interface ITagService {
    Page<Tag> getAllTags(int page, int size);
    Tag getTagById(Integer tagId);
    Tag saveTag(Tag tag);
    void deleteTag(Integer tagId);
}