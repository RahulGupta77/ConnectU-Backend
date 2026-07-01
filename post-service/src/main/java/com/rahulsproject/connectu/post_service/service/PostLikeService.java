package com.rahulsproject.connectu.post_service.service;

import com.rahulsproject.connectu.post_service.entity.PostLike;
import com.rahulsproject.connectu.post_service.exception.BadRequestException;
import com.rahulsproject.connectu.post_service.exception.ResourceNotFoundException;
import com.rahulsproject.connectu.post_service.repository.PostLikeRepository;
import com.rahulsproject.connectu.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public void likePost(Long postId, long userId) {

        postRepository.findById(postId).orElseThrow(()->
                new ResourceNotFoundException("Post not found with id: {}" + postId));

        log.info("Attempting to like post for postId: {}", postId);

        boolean isPostExists = postRepository.existsById(postId);

        if(!isPostExists) throw new ResourceNotFoundException("Post not found with id: " + postId);

        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if(isAlreadyLiked) throw new BadRequestException("Post is already liked");

        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);

        postLikeRepository.save(postLike);

        log.info("Successfully liked the post with postId {}", postId);

    }

    public void unlikePost(Long postId, long userId) {
        log.info("Attempting to unlike post for postId: {}", postId);

        boolean isPostExists = postRepository.existsById(postId);

        if(!isPostExists) throw new ResourceNotFoundException("Post not found with id: " + postId);

        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if(!isAlreadyLiked) throw new BadRequestException("Post is already unliked, cannot unlike the post");

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);

        log.info("Successfully unliked the post with postId {}", postId);
    }
}
