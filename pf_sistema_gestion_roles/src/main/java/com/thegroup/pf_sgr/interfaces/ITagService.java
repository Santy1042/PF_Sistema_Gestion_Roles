package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Tag;
import java.util.List;

public interface ITagService {
    List<Tag> getAllTags();
    Tag getTagById(Integer id);
    Tag saveTag(Tag tag);
    void deleteTag(Integer id);
}