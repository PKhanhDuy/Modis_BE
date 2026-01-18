package com.example.modis.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.post.dto.*;
import com.example.modis.post.model.Post;
import com.example.modis.post.model.Receiver;
import com.example.modis.post.repository.PostRepository;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    @Qualifier("postRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;
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
        Set<String> userIds = new HashSet<>();
        userIds.add(post.getSenderId());
        if(post.getReceivers() != null) post.getReceivers().forEach(r -> userIds.add(r.getReceiverId()));
        Map<String, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return mapToFullDTO(post, userMap);
    }

    private PostResponse mapToFullDTO(Post post, Map<String, User> userMap) {
        User sender = userMap.getOrDefault(post.getSenderId(), null);
        String sName = (sender != null) ? sender.getFullname() : "Unknown"; // Hoặc getUsername tùy model
        String sAvatar = (sender != null) ? sender.getAvatarUrl() : "";

        List<ReceiverDTO> receiverDtos = new ArrayList<>();
        if (post.getReceivers() != null) {
            receiverDtos = post.getReceivers().stream().map(r -> {
                User rUser = userMap.getOrDefault(r.getReceiverId(), null);
                return ReceiverDTO.builder()
                        .receiverId(r.getReceiverId())
                        .name((rUser != null) ? rUser.getFullname() : "Unknown")
                        .avatar((rUser != null) ? rUser.getAvatarUrl() : "")
                        .icon(r.getIcon())
                        .timestamp(r.getTimestamp())
                        .build();
            }).collect(Collectors.toList());
        }

        return PostResponse.builder()
                ._id(post.getId().toHexString())
                .senderId(post.getSenderId())
                .senderName(sName)
                .senderAvatar(sAvatar)
                .receivers(receiverDtos)
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
        Page<Post> pageResult;

        switch (request.getType()) {
            case "MINE":
                pageResult = postRepository.findBySenderId(request.getUserId(), pageable);
                break;
            case "FROM_SENDER":
                pageResult = postRepository.findPostsForMeFromSender(request.getUserId(), request.getSenderId(), pageable);
                break;
            default:
                pageResult = postRepository.findAllRelatedPosts(request.getUserId(), pageable);
                break;
        }
        rawPosts = pageResult.getContent();

        //Map sang DTO
        List<?> result;
        if ("GRID".equalsIgnoreCase(request.getViewMode())) {
            result = convertList(rawPosts, this::mapToSimpleDTO);
        } else {
            if (rawPosts.isEmpty()) {
                result = new ArrayList<>();
            } else {
                Set<String> userIds = new HashSet<>();
                for (Post post : rawPosts) {
                    userIds.add(post.getSenderId());
                    if (post.getReceivers() != null) {
                        post.getReceivers().forEach(r -> userIds.add(r.getReceiverId()));
                    }
                }

                // b. Query UserMap (Key: ID, Value: User)
                Map<String, User> userMap = userRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));

                // c. Map Post -> PostResponse (Full Info)
                result = rawPosts.stream()
                        .map(post -> mapToFullDTO(post, userMap))
                        .collect(Collectors.toList());
            }
        }

        //Lưu vào Redis
        if (!result.isEmpty()) {
            redisTemplate.opsForValue().set(redisKey, result, 15, TimeUnit.MINUTES);
            System.out.println("LOG: Lưu vào Redis thành công");
        }

        return result;
    }

    //GET POST DETAIL WITH REDIS
    public PostResponse getPostRedisById(String id) {
        String redisKey = "post:detail:" + id;
        PostResponse cachedPost = (PostResponse) redisTemplate.opsForValue().get(redisKey);
        if (cachedPost != null) {
            return cachedPost;
        }

        // Nếu không có trong Redis -> Lấy từ DB
        Post post = getPostById(id);
        Set<String> userIds = new HashSet<>();
        userIds.add(post.getSenderId());
        if(post.getReceivers() != null) post.getReceivers().forEach(r -> userIds.add(r.getReceiverId()));

        Map<String, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        PostResponse response = mapToFullDTO(post, userMap);
        //Lưu post này vào Redis
        redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.MINUTES);
        return response;
    }
}
