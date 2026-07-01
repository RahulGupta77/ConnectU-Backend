package com.rahulsproject.connectu.post_service.controller;

import com.rahulsproject.connectu.post_service.dto.PostCreateRequestDto;
import com.rahulsproject.connectu.post_service.dto.PostDto;
import com.rahulsproject.connectu.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostService postService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto, HttpServletRequest httpServletRequest){
        PostDto createdPost = postService.createPost(postCreateRequestDto, 1L);
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
