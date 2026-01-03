package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.repository.CandleRepository;
import com.ecomm.AuroraFlames.repository.CandleImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CandleService {

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private CandleImageRepository candleImageRepository;

    // Get all candles (including unavailable - for admin)
    public List<Candle> getAllCandles() {
        return candleRepository.findAll();
    }

    // Get only available candles (for public)
    public List<Candle> getAllAvailableCandles() {
        List<Candle> candles = candleRepository.findByAvailableTrue();
        // Force load images to avoid lazy loading issues
        candles.forEach(candle -> candle.getImages().size());
        return candles;
    }

    public Candle getCandleById(Long id) {
        Candle candle = candleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candle not found"));
        // Force load images to avoid lazy loading issues
        candle.getImages().size();
        return candle;
    }

    public List<Candle> searchCandlesByName(String name) {
        List<Candle> candles = candleRepository.findByNameContainingIgnoreCase(name);
        // Force load images to avoid lazy loading issues
        candles.forEach(candle -> candle.getImages().size());
        return candles;
    }

    // Get featured candles for homepage
    public List<Candle> getFeaturedCandles() {
        List<Candle> candles = candleRepository.findByFeaturedTrueAndAvailableTrue();
        // Force load images to avoid lazy loading issues
        candles.forEach(candle -> candle.getImages().size());
        return candles;
    }

    // Get candles by category (for variant switching)
    public List<Candle> getCandlesByCategory(String category) {
        List<Candle> candles = candleRepository.findByCategoryAndAvailableTrue(category);
        // Force load images to avoid lazy loading issues
        candles.forEach(candle -> candle.getImages().size());
        return candles;
    }

    public Candle saveCandle(Candle candle) {
        return candleRepository.save(candle);
    }

    public void deleteCandle(Long id) {
        candleRepository.deleteById(id);
    }

    // Image management methods
    public CandleImage saveCandleImage(CandleImage candleImage) {
        return candleImageRepository.save(candleImage);
    }

    public List<CandleImage> getCandleImages(Long candleId) {
        return candleImageRepository.findByCandleId(candleId);
    }

    public CandleImage getCandleImageById(Long id) {
        return candleImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void deleteCandleImage(Long imageId) {
        candleImageRepository.deleteById(imageId);
    }
}
