package com.example.modis.post.service;

import com.example.modis.post.dto.PostDTO;
import com.example.modis.post.dto.PostSimpleDTO;
import com.example.modis.post.model.Post;
import com.example.modis.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

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

    //Type: Chế độ lọc ảnh (gồm Mine: tôi, FROM_SENDER: lọc từ người gửi khác, ALL: tất cả)
    //ViewMode: chế độ xem (gồm LIST: xem ở trang chủ, GRID: xem ở trang AllImage
    public List<?> filterAndMapPosts(String userId, String type, String senderId, String viewMode) {
        List<Post> rawPosts;

        switch (type) {
            case "MINE":
                rawPosts = postRepository.findBySenderId(userId);
                break;
            case "FROM_SENDER":
                if (senderId == null) {
                    throw new IllegalArgumentException("senderId is required for FROM_SENDER");
                }
                rawPosts = postRepository.findPostsForMeFromSender(userId, senderId);
                break;
            case "ALL":
            default:
                rawPosts = postRepository.findAllRelatedPosts(userId);
                break;
        }


        if ("LIST".equalsIgnoreCase(viewMode)) {
            // Trang chủ: PostDTO
            return rawPosts.stream()
                    .map(this::mapToFullDTO)
                    .collect(Collectors.toList());
        } else {
            // Grid ảnh: SimplePostDTO
            return rawPosts.stream()
                    .map(this::mapToSimpleDTO)
                    .collect(Collectors.toList());
        }
    }
}
