package com.rahulsproject.connectu.post_service.controller;

import com.rahulsproject.connectu.post_service.dto.PostCreateRequestDto;
import com.rahulsproject.connectu.post_service.dto.PostDto;
import com.rahulsproject.connectu.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostsController {

    private final PostService postService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto, @RequestHeader("X-User-Id") Long userId){
        PostDto createdPost = postService.createPost(postCreateRequestDto, userId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId){
        PostDto postDto= postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsByUserId(@PathVariable Long userId){
        List<PostDto> allPostsList = postService.getAllPostsByUserId(userId);

        return ResponseEntity.ok(allPostsList);
    }
}
