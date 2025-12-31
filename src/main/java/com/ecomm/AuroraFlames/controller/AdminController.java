package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.service.CandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private CandleService candleService;

    private final String UPLOAD_DIR = "./uploads/";

    @PostMapping("/candles")
    public ResponseEntity<Candle> createCandle(@RequestBody Candle candle) {
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
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);
                }
            }
            return ResponseEntity.ok("Images uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload images");
        }
    }

    @GetMapping("/candles")
    public ResponseEntity<List<Candle>> getAllCandles() {
        List<Candle> candles = candleService.getAllAvailableCandles();
        return ResponseEntity.ok(candles);
    }
}
