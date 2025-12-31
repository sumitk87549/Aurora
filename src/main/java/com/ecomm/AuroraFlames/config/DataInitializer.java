package com.ecomm.AuroraFlames.config;

import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.repository.UserRepository;
import com.ecomm.AuroraFlames.repository.CandleRepository;
import com.ecomm.AuroraFlames.repository.CandleImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private CandleImageRepository candleImageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeSampleCandles();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail("prachiajaniya@gmail.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("prachiajaniya@gmail.com");
            admin.setPassword(passwordEncoder.encode("0000"));
            admin.setPhone("1234567890");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }
    }

    private void initializeSampleCandles() {
        if (candleRepository.count() == 0) {
            Candle candle1 = new Candle();
            candle1.setName("Lavender Dreams");
            candle1.setDescription("A calming lavender scented candle perfect for relaxation");
            candle1.setPrice(new BigDecimal("299.00"));
            candle1.setStockQuantity(50);
            candle1.setAvailable(true);
            candle1.setImages(new ArrayList<>());

            Candle savedCandle1 = candleRepository.save(candle1);

            CandleImage image1 = new CandleImage();
            image1.setCandle(savedCandle1);
            image1.setImageName("lavender-dreams.jpg");
            image1.setContentType("image/jpeg");
            image1.setImageUrl("/uploads/candles/lavender-dreams.jpg");
            candleImageRepository.save(image1);

            Candle candle2 = new Candle();
            candle2.setName("Vanilla Bliss");
            candle2.setDescription("Sweet vanilla aroma that creates a warm atmosphere");
            candle2.setPrice(new BigDecimal("349.00"));
            candle2.setStockQuantity(30);
            candle2.setAvailable(true);
            candle2.setImages(new ArrayList<>());

            Candle savedCandle2 = candleRepository.save(candle2);

            CandleImage image2 = new CandleImage();
            image2.setCandle(savedCandle2);
            image2.setImageName("vanilla-bliss.jpg");
            image2.setContentType("image/jpeg");
            image2.setImageUrl("/uploads/candles/vanilla-bliss.jpg");
            candleImageRepository.save(image2);

            Candle candle3 = new Candle();
            candle3.setName("Rose Garden");
            candle3.setDescription("Romantic rose fragrance for a beautiful ambiance");
            candle3.setPrice(new BigDecimal("399.00"));
            candle3.setStockQuantity(25);
            candle3.setAvailable(true);
            candle3.setImages(new ArrayList<>());

            Candle savedCandle3 = candleRepository.save(candle3);

            CandleImage image3 = new CandleImage();
            image3.setCandle(savedCandle3);
            image3.setImageName("rose-garden.jpg");
            image3.setContentType("image/jpeg");
            image3.setImageUrl("stored_in_db");
            image3.setImageData(new byte[] {}); // Empty placeholder
            candleImageRepository.save(image3);
        }
    }
}
