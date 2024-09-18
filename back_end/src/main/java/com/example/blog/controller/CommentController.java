package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.model.Comment;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.blog.model.User;
import java.util.List;
import com.example.blog.repository.UserRepository;
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin("*")
public class CommentController {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;  // Dodaj UserRepository

    // Injektuj UserRepository u konstruktor
    public CommentController(CommentRepository commentRepository, BlogPostRepository blogPostRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;  // Inicijalizuj UserRepository
    }

    // Get all comments for a specific post
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElse(null);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        List<Comment> comments = commentRepository.findByPost(post);
        return ResponseEntity.ok(comments);
    }

    // Add a comment to a post
    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        return blogPostRepository.findById(postId)
                .map(post -> {
                    User user = comment.getUser();
                    if (user != null) {
                        if (user.getId() == null) {
                            // Ako je ID korisnika null, vrati generički tip ResponseEntity<?> sa greškom
                            return ResponseEntity.badRequest().body("User ID cannot be null");
                        }
                        user = userRepository.findById(user.getId()).orElse(null);
                        if (user == null) {
                            // Ako korisnik nije pronađen, vrati generički tip ResponseEntity<?> sa greškom
                            return ResponseEntity.badRequest().body("User not found");
                        }
                        comment.setUser(user);  // Poveži korisnika sa komentarom
                    }

                    comment.setPost(post);  // Poveži post sa komentarom
                    Comment savedComment = commentRepository.save(comment);
                    // Uspešan odgovor, ovde vraćamo ResponseEntity<Comment>
                    return ResponseEntity.ok(savedComment);
                })
                // Ako post nije pronađen, vrati generički tip ResponseEntity<?> sa greškom
                .orElse(ResponseEntity.status(404).body("Post not found"));
    }








}
