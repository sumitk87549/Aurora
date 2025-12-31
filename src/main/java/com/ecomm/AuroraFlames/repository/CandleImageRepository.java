package com.ecomm.AuroraFlames.repository;

import com.ecomm.AuroraFlames.entity.CandleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandleImageRepository extends JpaRepository<CandleImage, Long> {
}
