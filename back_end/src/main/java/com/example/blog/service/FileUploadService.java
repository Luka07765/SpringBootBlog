package com.example.blog.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Ensure the upload directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique filename using UUID
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save the file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Return the relative URL to the image
        return "/uploads/" + uniqueFilename;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
