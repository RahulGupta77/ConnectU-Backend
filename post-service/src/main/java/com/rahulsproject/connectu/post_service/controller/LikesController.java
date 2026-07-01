package com.rahulsproject.connectu.post_service.controller;

import com.rahulsproject.connectu.post_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId){
        postLikeService.likePost(postId, userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId){
        postLikeService.unlikePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}
