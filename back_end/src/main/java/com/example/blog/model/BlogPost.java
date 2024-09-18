package com.example.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob  // Allows large content to be stored in the database
    private String content;

    @Column(nullable = false)
    private String author;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Field to store image URL or path
    private String imageUrl;

    // New relationships with comments and likes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    // Getters and Setters for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
