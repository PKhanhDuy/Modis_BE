package com.example.modis.post.controller;

import com.example.modis.post.model.Post;
import com.example.modis.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/posts")
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

    //Type: Chế độ lọc ảnh (gồm Mine: tôi, FROM_SENDER: lọc từ người gửi khác, ALL: tất cả)
    //ViewMode: chế độ xem (gồm LIST: xem ở trang chủ, GRID: xem ở trang AllImage
    @GetMapping("/filter")
    public ResponseEntity<?> filterPosts(
            @RequestParam String userId,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String senderId,
            @RequestParam(defaultValue = "LIST") String viewMode
    ) {
        List<?> result = postService.filterAndMapPosts(userId, type, senderId, viewMode);
        return ResponseEntity.ok(result);
    }

}
