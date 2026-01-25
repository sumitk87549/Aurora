package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.dto.CandleDTO;
import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.service.CandleService;
import com.ecomm.AuroraFlames.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candles")
public class CandleController {

    @Autowired
    private CandleService candleService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getPublicImage(@PathVariable Long id) {
        CandleImage image = candleService.getCandleImageById(id);

        // If image data is null, return a simple 1x1 transparent PNG
        if (image.getImageData() == null || image.getImageData().length == 0) {
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

        return ResponseEntity.ok()
                .header("Content-Type", image.getContentType())
                .header("Content-Disposition", "inline; filename=\"" + image.getImageName() + "\"")
                .body(image.getImageData());
    }

    @GetMapping
    public ResponseEntity<List<CandleDTO>> getAllAvailableCandles() {
        List<Candle> candles = candleService.getAllAvailableCandles();
        List<CandleDTO> candleDTOs = dtoMapper.toCandleDTOList(candles);
        return ResponseEntity.ok(candleDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandleDTO> getCandleById(@PathVariable Long id) {
        Candle candle = candleService.getCandleById(id);
        CandleDTO candleDTO = dtoMapper.toCandleDTO(candle);
        return ResponseEntity.ok(candleDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CandleDTO>> searchCandles(@RequestParam String name) {
        List<Candle> candles = candleService.searchCandlesByName(name);
        List<CandleDTO> candleDTOs = dtoMapper.toCandleDTOList(candles);
        return ResponseEntity.ok(candleDTOs);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<CandleDTO>> getFeaturedCandles() {
        List<Candle> candles = candleService.getFeaturedCandles();
        List<CandleDTO> candleDTOs = dtoMapper.toCandleDTOList(candles);
        return ResponseEntity.ok(candleDTOs);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CandleDTO>> getCandlesByCategory(@PathVariable String category) {
        List<Candle> candles = candleService.getCandlesByCategory(category);
        List<CandleDTO> candleDTOs = dtoMapper.toCandleDTOList(candles);
        return ResponseEntity.ok(candleDTOs);
    }
}
