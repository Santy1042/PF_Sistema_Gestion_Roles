package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ITagService;
import com.thegroup.pf_sgr.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @GetMapping("/getAllTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/getTagById")
    public ResponseEntity<Tag> getTagById(@RequestParam Integer id) {
        return tagService.getTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createTag")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.saveTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @DeleteMapping("/deleteTag")
    public ResponseEntity<Void> deleteTag(@RequestParam Integer id) {
        if (tagService.deleteTag(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}