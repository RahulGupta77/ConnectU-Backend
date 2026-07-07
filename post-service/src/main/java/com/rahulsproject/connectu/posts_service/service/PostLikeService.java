package com.rahulsproject.connectu.posts_service.service;

import com.rahulsproject.connectu.posts_service.auth.UserContextHolder;
import com.rahulsproject.connectu.posts_service.entity.Post;
import com.rahulsproject.connectu.posts_service.entity.PostLike;
import com.rahulsproject.connectu.posts_service.event.PostLikedEvent;
import com.rahulsproject.connectu.posts_service.exception.BadRequestException;
import com.rahulsproject.connectu.posts_service.exception.ResourceNotFoundException;
import com.rahulsproject.connectu.posts_service.repository.PostLikeRepository;
import com.rahulsproject.connectu.posts_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId) {

        Long userId = UserContextHolder.getCurrentUserId();

        postRepository.findById(postId).orElseThrow(()->
                new ResourceNotFoundException("Post not found with id: " + postId));

        log.info("Attempting to like post for postId: {} by userId: {}", postId, userId);

        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundException("Post not found with id: " + postId)
        );


        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if(isAlreadyLiked) throw new BadRequestException("Post is already liked");

        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);

        postLikeRepository.save(postLike);

        log.info("Successfully liked the post with postId {}", postId);

        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                .postId(postId)
                .creatorId(post.getUserId())
                .likedByUserId(userId)
                .build();

        kafkaTemplate.send("post-liked-topic", postId, postLikedEvent);

    }

    public void unlikePost(Long postId) {
        log.info("Attempting to unlike post for postId: {}", postId);

        Long userId = UserContextHolder.getCurrentUserId();

        boolean isPostExists = postRepository.existsById(postId);

        if(!isPostExists) throw new ResourceNotFoundException("Post not found with id: " + postId);

        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if(!isAlreadyLiked) throw new BadRequestException("Post is already unliked, cannot unlike the post");

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);

        log.info("Successfully unliked the post with postId {}", postId);
    }
}
