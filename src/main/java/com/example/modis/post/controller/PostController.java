package com.example.modis.post.controller;

import com.example.modis.post.dto.PostFilterRequest;
import com.example.modis.post.model.Post;
import com.example.modis.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<List<Post>>> getPostByUserId(@PathVariable String userId){
        return null;
    }

    @GetMapping("/pair")
    public CompletableFuture<ResponseEntity<List<Post>>> getPostBySenderIdAndReceiverId(@RequestParam String senderId, @RequestParam String receiverId){
        return null;
    }

    @GetMapping("/save")
    public CompletableFuture<ResponseEntity<Post>> savePost(@RequestBody Post post){
        return null;
    }

    @DeleteMapping("/delete/{postId}")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deletePostById(@PathVariable String postId){
        return null;
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPosts(@ModelAttribute PostFilterRequest request) {
        return ResponseEntity.ok(postService.filterAndMapPosts(request));
    }

}
