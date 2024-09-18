package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import com.example.blog.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin("*")
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private FileUploadService fileUploadService;  // Inject FileUploadService to handle image uploads

    @GetMapping
    public List<BlogPost> getAllPosts() {
        return blogPostService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long id) {
        BlogPost blogPost = blogPostService.getPostById(id);
        if (blogPost != null) {
            return ResponseEntity.ok(blogPost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public BlogPost createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setAuthor(author);

        // Handle image upload if an image is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileUploadService.saveImage(image);  // Save image and get URL/path
            blogPost.setImageUrl(imageUrl);  // Set the image URL in the blog post
        }

        return blogPostService.createPost(blogPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        BlogPost blogPost = blogPostService.getPostById(id);
        if (blogPost == null) {
            return ResponseEntity.notFound().build();
        }

        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setAuthor(author);

        // Handle image upload if a new image is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileUploadService.saveImage(image);  // Save new image and get URL/path
            blogPost.setImageUrl(imageUrl);  // Update the image URL
        }

        BlogPost updatedPost = blogPostService.updatePost(id, blogPost);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
