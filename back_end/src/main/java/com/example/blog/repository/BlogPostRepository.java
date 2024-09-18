package com.example.blog.repository;

import com.example.blog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
}
