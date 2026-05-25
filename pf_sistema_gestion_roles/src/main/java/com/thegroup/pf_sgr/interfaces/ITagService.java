package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Tag;
import java.util.List;
import java.util.Optional;

public interface ITagService {
    List<Tag> getAllTags();
    Optional<Tag> getTagById(Integer id);
    Tag saveTag(Tag tag);
    boolean deleteTag(Integer id);
}