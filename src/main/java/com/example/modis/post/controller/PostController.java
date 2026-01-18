package com.example.modis.post.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.post.dto.*;
import com.example.modis.post.model.Post;
import com.example.modis.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final Cloudinary cloudinary;

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
        System.out.println("Da nhan duoc post moi");
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable String postId
    ) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/react")
    public ResponseEntity<PostResponse> updatePost(
            @RequestBody ReactCaptionRequest request
    ) {
        System.out.println("Da nhan duoc react request");
        PostResponse updated = postService.reactToPost(request.getPostId(), request.getSenderId(), request.getReaction());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPosts(@ModelAttribute PostFilterRequest request) {
        return ResponseEntity.ok(postService.filterAndMapPosts(request));
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File ảnh rỗng"));
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "Modis")
            );

            String url = (String) uploadResult.get("secure_url");
            System.out.println("anh da duoc upload la " + url);
            return ResponseEntity.ok(Map.of("url", url));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
