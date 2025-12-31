package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Candle;
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
