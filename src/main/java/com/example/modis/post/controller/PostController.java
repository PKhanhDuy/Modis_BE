package com.example.modis.post.controller;

import com.example.modis.post.dto.PostDto;
import com.example.modis.post.dto.PostRequest;
import com.example.modis.post.dto.PostFilterRequest;
import com.example.modis.post.model.Post;
import com.example.modis.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(
            @RequestBody PostRequest request
    ) {
        PostDto postDto = postService.createPost(
                request.getSenderId(),
                request.getReceivers(),
                request.getCaption(),
                request.getUrlImage()
        );
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable String postId
    ) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPosts(@ModelAttribute PostFilterRequest request) {
        return ResponseEntity.ok(postService.filterAndMapPosts(request));
    }

}
