package com.ecomm.AuroraFlames.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private long totalProducts;
    private long outOfStockProducts;
    private long totalUsers;
    private long newUsersToday;
}
