package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.service.CandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candles")
@CrossOrigin(origins = "http://localhost:4200")
public class CandleController {

    @Autowired
    private CandleService candleService;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getPublicImage(@PathVariable Long id) {
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

    @GetMapping
    public ResponseEntity<List<Candle>> getAllAvailableCandles() {
        List<Candle> candles = candleService.getAllAvailableCandles();
        return ResponseEntity.ok(candles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candle> getCandleById(@PathVariable Long id) {
        Candle candle = candleService.getCandleById(id);
        return ResponseEntity.ok(candle);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Candle>> searchCandles(@RequestParam String name) {
        List<Candle> candles = candleService.searchCandlesByName(name);
        return ResponseEntity.ok(candles);
    }
}
