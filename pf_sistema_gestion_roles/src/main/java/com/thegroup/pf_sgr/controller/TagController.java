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
@CrossOrigin("*")
public class TagController {

    private final ITagService tagService;

    @GetMapping("/getAllTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/getTagById")
    public ResponseEntity<Tag> getTagById(@RequestParam Integer id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping("/createTag")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.saveTag(tag));
    }

    @DeleteMapping("/deleteTag")
    public ResponseEntity<Void> deleteTag(@RequestParam Integer id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}