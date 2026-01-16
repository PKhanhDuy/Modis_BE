package com.example.modis.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.post.dto.*;
import com.example.modis.post.model.Post;
import com.example.modis.post.model.Receiver;
import com.example.modis.post.repository.PostRepository;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public PostDto toDTO(Post post) {
        return PostDto.builder()
                .senderId(post.getSenderId())
                .receivers(post.getReceivers())
                .caption(post.getCaption())
                .urlImage(post.getUrlImage())
                .created_at(post.getCreated_at())
                .build();
    }

    /* ================= CREATE POST ================= */

    public PostDto createPost(
            String senderId,
            List<Receiver> receivers,
            String caption,
            String urlImage
    ) {
        // validate user
        userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        if (urlImage == null || urlImage.isEmpty()) {
            throw new IllegalArgumentException("Ảnh bài post không hợp lệ");
        }

        // upload ảnh
        String imageUrl = uploadPostImage(senderId, urlImage);
//        String imageUrl = urlImage;

        // tạo post
        Post newPost = Post.builder()
                .senderId(senderId)
                .receivers(receivers)
                .caption(caption)
                .urlImage(imageUrl)
                .created_at(new Date().toInstant())
                .build();

        Post post = postRepository.save((newPost));

        return toDTO(post);
    }

    /* ================= GET ================= */

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
    }

    /* ================= DELETE ================= */

    public void deletePost(String id) {
        Post post = getPostById(id);
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
    private PostResponse mapToFullDTO(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .senderId(post.getSenderId())
                .receivers(post.getReceivers())
                .caption(post.getCaption())
                .urlImage(post.getUrlImage())
                .created_at(post.getCreated_at())
                .build();
    }

    public PostSimpleDTO mapToSimpleDTO(Post post){
        return PostSimpleDTO.builder()
                .id(post.getId())
                .urlImage(post.getUrlImage())
                .created_at(post.getCreated_at())
                .build();
    }

    private <T> List<T> convertList(List<Post> posts, Function<Post, T> mapper) {
        return posts.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    //Type: Chế độ lọc ảnh (gồm Mine: tôi, FROM_SENDER: lọc từ người gửi khác, ALL: tất cả)
    //ViewMode: chế độ xem (gồm LIST: xem ở trang chủ, GRID: xem ở trang AllImage
    public List<?> filterAndMapPosts(PostFilterRequest request) {
        //Check Redis
        String redisKey = request.toRedisKey();
        List<?> cachedData = (List<?>) redisTemplate.opsForValue().get(redisKey);

        if (cachedData != null) {
            System.out.println("LOG: Lấy dữ liệu từ Redis");
            return cachedData;
        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "created_at")
        );

        List<Post> rawPosts;
        switch (request.getType()) {
            case "MINE":
                rawPosts = postRepository.findBySenderId(request.getUserId(), pageable);
                break;
            case "FROM_SENDER":
                rawPosts = postRepository.findPostsForMeFromSender(request.getUserId(), request.getSenderId(), pageable);
                break;
            default: // ALL
                rawPosts = postRepository.findAllRelatedPosts(request.getUserId(), pageable);
                break;
        }

        //Map sang DTO
        List<?> result;
        if ("GRID".equalsIgnoreCase(request.getViewMode())) {
            result = convertList(rawPosts, this::mapToSimpleDTO);
        } else {
            result = convertList(rawPosts, this::mapToFullDTO);
        }

        //Lưu vào Redis
        if (!result.isEmpty()) {
            redisTemplate.opsForValue().set(redisKey, result, 15, TimeUnit.MINUTES);
            System.out.println("LOG: Lưu vào Redis thành công");
        }

        return result;
    }
}
