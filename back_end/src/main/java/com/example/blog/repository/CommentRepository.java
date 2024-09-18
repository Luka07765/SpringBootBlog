package com.example.blog.repository;

import com.example.blog.model.Comment;
import com.example.blog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Find all comments for a specific post
    List<Comment> findByPost(BlogPost post);
}
