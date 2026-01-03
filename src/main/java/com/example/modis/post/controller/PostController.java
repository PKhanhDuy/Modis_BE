package com.example.modis.post.controller;

import com.example.modis.post.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/posts")
public class PostController {

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

}
