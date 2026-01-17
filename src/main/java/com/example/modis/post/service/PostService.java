package com.example.modis.post.service;

import com.example.modis.post.dto.PostDTO;
import com.example.modis.post.dto.PostFilterRequest;
import com.example.modis.post.dto.PostSimpleDTO;
import com.example.modis.post.model.Post;
import com.example.modis.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    @Qualifier("postRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public Post save(Post post) {
        return postRepository.save(post);
    }

    private PostDTO mapToFullDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .senderId(post.getSenderId())
                .receiver(post.getReceiver())
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
