package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// BaseUploadController.java
public class FileController {
    protected final ImageService imageService;

    public FileController(ImageService imageService) {
        this.imageService = imageService;
    }

    protected ResponseEntity<String> handleUpload(Long id, ObjectType type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty");
        }

        try {
            String url = imageService.uploadAndSave(id, type, file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        }
    }

    protected void handleDisableImage(Long categoryId, ObjectType type) {
        try {
            imageService.disableImageByObject(categoryId, type);
            ResponseEntity.ok("Image disabled successfully");
        } catch (ResourceNotFoundException e) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found: " + e.getMessage());
        } catch (IllegalStateException e) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to disable image: " + e.getMessage());
        }
    }
}