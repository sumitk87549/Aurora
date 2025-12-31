package com.ecomm.AuroraFlames.repository;

import com.ecomm.AuroraFlames.entity.Order;
import com.ecomm.AuroraFlames.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserOrderByOrderDateDesc(User user);
}
