package com.example.blog.controller;
import java.util.Map;
import java.util.HashMap;

import com.example.blog.model.BlogPost;
import com.example.blog.model.Like;
import com.example.blog.model.User;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.repository.LikeRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@CrossOrigin("*")
public class LikeController {

    private final LikeRepository likeRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    public LikeController(LikeRepository likeRepository, BlogPostRepository blogPostRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
    }
//        if (principal == null) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//    }
    // Like a post
@PostMapping
public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, Principal principal) {
    if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    String email = principal.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

    // Check if the user already liked the post
    if (likeRepository.findByPostAndUser(post, user).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);  // Prevent multiple likes from the same user
    }

    // Save the like
    Like like = new Like();
    like.setPost(post);
    like.setUser(user);
    likeRepository.save(like);

    // Get the updated like count
    long likeCount = likeRepository.countByPost(post);

    // Prepare the response with the updated like count
    Map<String, Object> response = new HashMap<>();
    response.put("likeCount", likeCount);

    return ResponseEntity.ok(response);
}



}