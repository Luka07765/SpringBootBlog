package com.example.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore  // Prevent circular reference by ignoring the post during serialization
    private BlogPost post;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;


    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}
