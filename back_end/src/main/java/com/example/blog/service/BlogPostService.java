package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id).orElse(null);
    }

    public BlogPost createPost(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    public BlogPost updatePost(Long id, BlogPost updatedPost) {
        BlogPost existingPost = getPostById(id);
        if (existingPost != null) {
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setAuthor(updatedPost.getAuthor());
            return blogPostRepository.save(existingPost);
        }
        return null;
    }

    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
    }
}
