package com.example.modis.post.repository;

import com.example.modis.post.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    //Tìm tất cả post liên quan đến user hiện tại
    @Query("{ '$or': [ { 'receiver.receiverId': ?0 }, { 'senderId': ?0 } ] }")
    List<Post> findAllRelatedPosts(String currentUserId);

    //Tìm danh sách post của người khác gửi cho mình
    @Query("{ 'receiver.receiverId': ?0, 'senderId': ?1 }")
    List<Post> findPostsForMeFromSender(String currentUserId, String senderId);

    //Tìm danh sách post do chính mình gửi
    List<Post> findBySenderId(String senderId);
}
