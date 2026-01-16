package com.example.modis.post.controller;

import com.example.modis.post.dto.PostDTO;
import com.example.modis.post.dto.PostFilterRequest;
import com.example.modis.post.model.Post;
import com.example.modis.post.model.Receiver;
import com.example.modis.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/pair")
    public CompletableFuture<ResponseEntity<List<Post>>> getPostBySenderIdAndReceiverId(@RequestParam String senderId, @RequestParam String receiverId){
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(
            @RequestBody PostDTO request
    ) {
        PostDTO postDto = postService.createPost(
                request.getSenderId(),
                request.getReceiver(),
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
