package com.ecomm.AuroraFlames.repository;

import com.ecomm.AuroraFlames.entity.Wishlist;
import com.ecomm.AuroraFlames.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser(User user);
}
