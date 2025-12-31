package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.service.CandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private CandleService candleService;

    @PostMapping("/candles")
    public ResponseEntity<Candle> createCandle(@RequestBody Candle candle) {
        // Ensure ID is null for new entities to avoid conflicts
        candle.setId(null);
        Candle savedCandle = candleService.saveCandle(candle);
        return ResponseEntity.ok(savedCandle);
    }

    @PutMapping("/candles/{id}")
    public ResponseEntity<Candle> updateCandle(@PathVariable Long id, @RequestBody Candle candle) {
        candle.setId(id);
        Candle updatedCandle = candleService.saveCandle(candle);
        return ResponseEntity.ok(updatedCandle);
    }

    @DeleteMapping("/candles/{id}")
    public ResponseEntity<Void> deleteCandle(@PathVariable Long id) {
        candleService.deleteCandle(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/candles/{id}/images")
    public ResponseEntity<String> uploadImages(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            System.out.println("Uploading images for candle ID: " + id);
            System.out.println("Number of files: " + files.length);
            
            Candle candle = candleService.getCandleById(id);
            
            // Create upload directory if it doesn't exist
            String uploadDir = "uploads/candles/";
            java.io.File directory = new java.io.File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                System.out.println("Processing file " + i + ": " + file.getOriginalFilename() +
                        ", size: " + file.getSize() + ", contentType: " + file.getContentType());
                
                if (!file.isEmpty()) {
                    // Generate unique filename
                    String originalFilename = file.getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = System.currentTimeMillis() + "_" + i + fileExtension;
                    
                    // Save file to disk
                    String filePath = uploadDir + uniqueFilename;
                    java.io.File destFile = new java.io.File(filePath);
                    file.transferTo(destFile);
                    
                    // Save image reference to database
                    CandleImage candleImage = new CandleImage();
                    candleImage.setImageName(originalFilename);
                    candleImage.setImageUrl("/uploads/candles/" + uniqueFilename);
                    candleImage.setContentType(file.getContentType());
                    candleImage.setCandle(candle);
                    
                    CandleImage savedImage = candleService.saveCandleImage(candleImage);
                    System.out.println("Saved image with ID: " + savedImage.getId() + " at URL: " + candleImage.getImageUrl());
                } else {
                    System.out.println("File " + i + " is empty");
                }
            }
            return ResponseEntity.ok("Images uploaded successfully to file system");
        } catch (Exception e) {
            System.err.println("Error uploading images: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload images: " + e.getMessage());
        }
    }

    @GetMapping("/candles")
    public ResponseEntity<List<Candle>> getAllCandles() {
        List<Candle> candles = candleService.getAllAvailableCandles();
        return ResponseEntity.ok(candles);
    }

    @GetMapping("/candles/{id}/images")
    public ResponseEntity<List<CandleImage>> getCandleImages(@PathVariable Long id) {
        List<CandleImage> images = candleService.getCandleImages(id);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        CandleImage image = candleService.getCandleImageById(id);
        
        try {
            // If image has a URL, serve from file system
            if (image.getImageUrl() != null && !image.getImageUrl().isEmpty()) {
                String filePath = image.getImageUrl().replace("/uploads/", "uploads/");
                java.io.File file = new java.io.File(filePath);
                
                if (file.exists()) {
                    byte[] imageBytes = java.nio.file.Files.readAllBytes(file.toPath());
                    return ResponseEntity.ok()
                        .header("Content-Type", image.getContentType())
                        .header("Content-Disposition", "inline; filename=\"" + image.getImageName() + "\"")
                        .body(imageBytes);
                }
            }
            
            // Fallback: return placeholder if no file found
            byte[] defaultImage = new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44,
                    0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15,
                    (byte) 0xC4, (byte) 0x89,
                    0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
                    0x05, 0x00, 0x01,
                    0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42,
                    0x60, (byte) 0x82
            };
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Content-Disposition", "inline; filename=\"placeholder.png\"")
                    .body(defaultImage);
                    
        } catch (Exception e) {
            System.err.println("Error serving image: " + e.getMessage());
            // Return placeholder on error
            byte[] defaultImage = new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44,
                    0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15,
                    (byte) 0xC4, (byte) 0x89,
                    0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
                    0x05, 0x00, 0x01,
                    0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42,
                    0x60, (byte) 0x82
            };
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Content-Disposition", "inline; filename=\"placeholder.png\"")
                    .body(defaultImage);
        }
    }
}
