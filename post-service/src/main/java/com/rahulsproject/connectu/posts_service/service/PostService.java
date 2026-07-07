package com.rahulsproject.connectu.posts_service.service;

import com.rahulsproject.connectu.posts_service.auth.UserContextHolder;
import com.rahulsproject.connectu.posts_service.clients.ConnectionsClient;
import com.rahulsproject.connectu.posts_service.dto.PersonDto;
import com.rahulsproject.connectu.posts_service.dto.PostCreateRequestDto;
import com.rahulsproject.connectu.posts_service.dto.PostDto;
import com.rahulsproject.connectu.posts_service.entity.Post;
import com.rahulsproject.connectu.posts_service.event.PostCreatedEvent;
import com.rahulsproject.connectu.posts_service.exception.ResourceNotFoundException;
import com.rahulsproject.connectu.posts_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsClient connectionsClient;

    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto) {

        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        Long userId = UserContextHolder.getCurrentUserId();

        post.setUserId(userId);

        Post savedPost = postRepository.save(post);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder().
                postId(savedPost.getId())
                .creatorId(userId)
                .content(post.getContent())
                .build();

        kafkaTemplate.send("post-created-topic", postCreatedEvent);

        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {

        log.debug("Retrieving Post by Id: {}", postId);

        List<PersonDto> firstDegreeConnections =  connectionsClient.getFirstConnections();

        log.info("First Degree Connections: {}", firstDegreeConnections);

        Post post = postRepository.findById(postId).orElseThrow(()->
                new ResourceNotFoundException("Post not found with Id: "+ postId));

        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsByUserId(Long userId) {

        log.info("Fetching all posts of userId: {}", userId);

        List<Post> allSavedPosts = postRepository.findByUserId(userId);

        if(allSavedPosts.isEmpty()){
            throw new ResourceNotFoundException("No posts found with userId: "+ userId);
        }

        return allSavedPosts.stream()
                .map(post-> modelMapper.map(post, PostDto.class))
                .toList();
    }

}
