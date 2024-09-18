package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "`like`")  // Escaping the "like" keyword with backticks
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference  // Prevent infinite recursion during serialization
    private BlogPost post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference  // Prevent infinite recursion during serialization
    private User user;
}
