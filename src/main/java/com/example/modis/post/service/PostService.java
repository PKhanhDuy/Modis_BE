package com.example.modis.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.post.dto.*;
import com.example.modis.post.model.Post;
import com.example.modis.post.model.Receiver;
import com.example.modis.post.repository.PostRepository;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Qualifier("postRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    private final UserRepository userRepository;

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
        System.out.println("Dang tao post");
        // tạo post
        Post newPost = Post.builder()
                .senderId(senderId)
                .receivers(receivers)
                .caption(caption)
                .urlImage(urlImage)
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


    private PostResponse mapToFullDTO(Post post) {
        return PostResponse.builder()
                .id(post.getId().toHexString())
                .senderId(post.getSenderId())
                .receivers(post.getReceivers())
                .caption(post.getCaption())
                .urlImage(post.getUrlImage())
                .created_at(post.getCreated_at())
                .build();
    }

    public PostSimpleDTO mapToSimpleDTO(Post post){
        return PostSimpleDTO.builder()
                .id(post.getId().toHexString())
                .urlImage(post.getUrlImage())
                .created_at(post.getCreated_at())
                .build();
    }

    private <T> List<T> convertList(List<Post> posts, Function<Post, T> mapper) {
        return posts.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public PostResponse reactToPost(String postId, String receiverId, String icon) {

        Query query = new Query(
                Criteria.where("_id").is(new ObjectId(postId))
                        .and("receivers.receiverId").is(receiverId)
        );

        Update update = new Update()
                .set("receivers.$.icon", icon)
                .set("receivers.$.timestamp", Instant.now());

        mongoTemplate.updateFirst(query, update, Post.class);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToFullDTO(post);
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

        System.out.println("request gui len la "+ request);
        //Map sang DTO
        List<?> result;
        if ("GRID".equalsIgnoreCase(request.getViewMode())) {
            result = convertList(rawPosts, this::mapToSimpleDTO);
        } else {
            result = convertList(rawPosts, this::mapToFullDTO);
        }
        System.out.println("danh sach lay ra la " + rawPosts);

        //Lưu vào Redis
        if (!result.isEmpty()) {
            redisTemplate.opsForValue().set(redisKey, result, 15, TimeUnit.MINUTES);
            System.out.println("LOG: Lưu vào Redis thành công");
        }
        return result;
    }
}
