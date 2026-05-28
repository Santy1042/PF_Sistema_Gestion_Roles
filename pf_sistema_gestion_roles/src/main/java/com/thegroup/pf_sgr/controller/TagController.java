package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ITagService;
import com.thegroup.pf_sgr.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @GetMapping
    public ResponseEntity<Page<Tag>> getAllTags(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(tagService.getAllTags(page, size));
    }

    @GetMapping("/getTagById")
    public ResponseEntity<Tag> getTagById(@RequestParam Integer tagId) {
        return ResponseEntity.ok(tagService.getTagById(tagId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createTag")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.saveTag(tag));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteTag")
    public ResponseEntity<Void> deleteTag(@RequestParam Integer tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}