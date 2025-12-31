package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.repository.CandleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandleService {

    @Autowired
    private CandleRepository candleRepository;

    public List<Candle> getAllAvailableCandles() {
        return candleRepository.findByAvailableTrue();
    }

    public Candle getCandleById(Long id) {
        return candleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candle not found"));
    }

    public List<Candle> searchCandlesByName(String name) {
        return candleRepository.findByNameContainingIgnoreCase(name);
    }

    public Candle saveCandle(Candle candle) {
        return candleRepository.save(candle);
    }

    public void deleteCandle(Long id) {
        candleRepository.deleteById(id);
    }
}
