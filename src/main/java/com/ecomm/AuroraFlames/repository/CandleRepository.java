package com.ecomm.AuroraFlames.repository;

import com.ecomm.AuroraFlames.entity.Candle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandleRepository extends JpaRepository<Candle, Long> {
    List<Candle> findByAvailableTrue();

    List<Candle> findByNameContainingIgnoreCase(String name);

    List<Candle> findByFeaturedTrueAndAvailableTrue();

    List<Candle> findByCategoryAndAvailableTrue(String category);
}
