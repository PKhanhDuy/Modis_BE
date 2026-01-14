package com.example.modis.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.post.model.Post;
import com.example.modis.post.model.Receiver;
import com.example.modis.post.repository.PostRepository;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    /* ================= CREATE POST ================= */

    public Post createPost(
            String senderId,
            Receiver receiver,
            String caption,
            String urlImage,
            Date created_at
    ) {

        // validate user
        userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        if (urlImage == null || urlImage.isEmpty()) {
            throw new IllegalArgumentException("Ảnh bài post không hợp lệ");
        }

        // upload ảnh
        String imageUrl = uploadPostImage(senderId, urlImage);

        // tạo post
        Post post = Post.builder()
                .senderId(senderId)
                .receiver(receiver)
                .caption(caption)
                .urlImage(imageUrl)
                .created_at(new Date())
                .build();

        return postRepository.save(post);
    }

    /* ================= GET ================= */

    public Post getPostBySender(String senderId) {
        return postRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
    }

    /* ================= DELETE ================= */

    public void deletePost(String senderId) {
        Post post = getPostBySender(senderId);
        postRepository.delete(post);
    }

    /* ================= IMAGE UPLOAD ================= */

    private String uploadPostImage(String senderId, String image) {

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "Modis/posts/" + senderId,
                            "public_id", senderId + "_post",
                            "overwrite", true
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new RuntimeException("Cloudinary không trả về URL ảnh");
            }

            return secureUrl.toString();

        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh post thất bại", e);
        }
    }
}
