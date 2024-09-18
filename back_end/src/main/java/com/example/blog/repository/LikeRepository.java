package com.example.blog.repository;

import com.example.blog.model.Like;
import com.example.blog.model.BlogPost;
import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a user has already liked a specific post
    Optional<Like> findByPostAndUser(BlogPost post, User user);

    // Find all likes for a specific post
    List<Like> findByPost(BlogPost post);

    // Count the number of likes for a specific post
    long countByPost(BlogPost post);  // This will count the number of likes for a post
}
