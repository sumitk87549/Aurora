package com.ecomm.AuroraFlames.repository;

import com.ecomm.AuroraFlames.entity.Cart;
import com.ecomm.AuroraFlames.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
