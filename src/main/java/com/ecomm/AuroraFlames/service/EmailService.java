package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.entity.Order;
import com.ecomm.AuroraFlames.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${aurora.business.name:Aurora Flames}")
    private String businessName;

    @Value("${aurora.business.email:}")
    private String businessEmail;

    @Value("${aurora.business.phone:}")
    private String businessPhone;

    @Value("${aurora.business.instagram:}")
    private String instagramHandle;

    @Value("${aurora.admin.email:}")
    private String adminEmail;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Check if email is properly configured
     */
    public boolean isEmailConfigured() {
        return mailSender != null &&
                senderEmail != null && !senderEmail.isEmpty() &&
                !senderEmail.equals("YOUR_EMAIL@gmail.com");
    }

    /**
     * Send order confirmation email to customer
     */
    @Async
    public void sendOrderConfirmationToCustomer(Order order) {
        if (!isEmailConfigured()) {
            System.out.println(
                    "Email not configured. Skipping customer notification for order: " + order.getOrderNumber());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Order Confirmed! 🕯️ " + order.getOrderNumber() + " - " + businessName);

            String htmlContent = buildCustomerEmailContent(order);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Order confirmation email sent to: " + order.getCustomerEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send customer email: " + e.getMessage());
        }
    }

    /**
     * Send new order notification to admin
     */
    @Async
    public void sendOrderNotificationToAdmin(Order order) {
        if (!isEmailConfigured() || adminEmail == null || adminEmail.isEmpty()) {
            System.out.println(
                    "Admin email not configured. Skipping admin notification for order: " + order.getOrderNumber());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(adminEmail);
            helper.setSubject("🔔 New Order Received! " + order.getOrderNumber());

            String htmlContent = buildAdminEmailContent(order);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Order notification email sent to admin: " + adminEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send admin email: " + e.getMessage());
        }
    }

    private String buildCustomerEmailContent(Order order) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
        html.append("<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // Header
        html.append(
                "<div style='text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; border-radius: 10px 10px 0 0;'>");
        html.append("<h1 style='color: white; margin: 0;'>🕯️ ").append(businessName).append("</h1>");
        html.append("<p style='color: white; opacity: 0.9;'>Thank you for your order!</p>");
        html.append("</div>");

        // Order details
        html.append("<div style='background: #f8f9fa; padding: 20px; border-radius: 0 0 10px 10px;'>");
        html.append("<h2 style='color: #333;'>Order Confirmed ✓</h2>");
        html.append("<p><strong>Order Number:</strong> ").append(order.getOrderNumber()).append("</p>");
        html.append("<p><strong>Order Date:</strong> ").append(order.getOrderDate().format(dateFormatter))
                .append("</p>");
        html.append("<p><strong>Payment Method:</strong> ").append(order.getPaymentMethod()).append("</p>");

        if (order.getExpectedDeliveryDate() != null) {
            html.append("<p><strong>Expected Delivery:</strong> ")
                    .append(order.getExpectedDeliveryDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")))
                    .append("</p>");
        }

        // Order items
        html.append("<h3 style='margin-top: 20px;'>Order Items</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append(
                "<tr style='background: #667eea; color: white;'><th style='padding: 10px; text-align: left;'>Item</th><th style='padding: 10px;'>Qty</th><th style='padding: 10px; text-align: right;'>Price</th></tr>");

        for (OrderItem item : order.getOrderItems()) {
            html.append("<tr style='border-bottom: 1px solid #ddd;'>");
            html.append("<td style='padding: 10px;'>").append(item.getCandle().getName()).append("</td>");
            html.append("<td style='padding: 10px; text-align: center;'>").append(item.getQuantity()).append("</td>");
            html.append("<td style='padding: 10px; text-align: right;'>₹")
                    .append(item.getPriceAtTime().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .append("</td>");
            html.append("</tr>");
        }

        html.append("<tr style='background: #f0f0f0; font-weight: bold;'>");
        html.append("<td colspan='2' style='padding: 10px;'>Total</td>");
        html.append("<td style='padding: 10px; text-align: right;'>₹").append(order.getTotalAmount()).append("</td>");
        html.append("</tr></table>");

        // Shipping address
        html.append("<h3 style='margin-top: 20px;'>Shipping Address</h3>");
        html.append("<p>").append(order.getCustomerName()).append("<br>");
        html.append(order.getShippingAddress()).append("<br>");
        html.append("Phone: ").append(order.getCustomerPhone()).append("</p>");

        // Footer
        html.append(
                "<div style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center;'>");
        html.append("<p style='color: #666;'>Questions? Contact us!</p>");
        if (!businessPhone.isEmpty()) {
            html.append("<p>📞 ").append(businessPhone).append("</p>");
        }
        if (!instagramHandle.isEmpty()) {
            html.append("<p>📷 Instagram: ").append(instagramHandle).append("</p>");
        }
        html.append("<p style='color: #999; font-size: 12px;'>Thank you for shopping with ").append(businessName)
                .append("! 🕯️✨</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String buildAdminEmailContent(Order order) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
        html.append("<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // Header
        html.append(
                "<div style='background: #28a745; padding: 20px; border-radius: 10px 10px 0 0; text-align: center;'>");
        html.append("<h1 style='color: white; margin: 0;'>🔔 New Order Received!</h1>");
        html.append("</div>");

        // Order summary
        html.append("<div style='background: #f8f9fa; padding: 20px;'>");
        html.append("<h2>Order: ").append(order.getOrderNumber()).append("</h2>");
        html.append("<p><strong>Total Amount:</strong> <span style='font-size: 1.5em; color: #28a745;'>₹")
                .append(order.getTotalAmount()).append("</span></p>");
        html.append("<p><strong>Payment:</strong> ").append(order.getPaymentMethod()).append(" (")
                .append(order.getPaymentStatus()).append(")</p>");
        html.append("<p><strong>Date:</strong> ").append(order.getOrderDate().format(dateFormatter)).append("</p>");

        // Customer details (important for admin)
        html.append("<div style='background: white; padding: 15px; border-radius: 8px; margin: 15px 0;'>");
        html.append("<h3 style='margin-top: 0; color: #667eea;'>👤 Customer Details</h3>");
        html.append("<p><strong>Name:</strong> ").append(order.getCustomerName()).append("</p>");
        html.append("<p><strong>Phone:</strong> <a href='tel:").append(order.getCustomerPhone()).append("'>")
                .append(order.getCustomerPhone()).append("</a></p>");
        html.append("<p><strong>Email:</strong> ")
                .append(order.getCustomerEmail() != null ? order.getCustomerEmail() : "Not provided").append("</p>");
        html.append("<p><strong>Address:</strong><br>").append(order.getShippingAddress()).append("</p>");
        html.append("</div>");

        // Order items
        html.append("<h3>📦 Items Ordered</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse; background: white;'>");
        html.append(
                "<tr style='background: #667eea; color: white;'><th style='padding: 10px; text-align: left;'>Product</th><th style='padding: 10px;'>Qty</th><th style='padding: 10px; text-align: right;'>Price</th></tr>");

        for (OrderItem item : order.getOrderItems()) {
            html.append("<tr style='border-bottom: 1px solid #eee;'>");
            html.append("<td style='padding: 10px;'>").append(item.getCandle().getName()).append("</td>");
            html.append("<td style='padding: 10px; text-align: center;'>").append(item.getQuantity()).append("</td>");
            html.append("<td style='padding: 10px; text-align: right;'>₹")
                    .append(item.getPriceAtTime().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        // Order notes if any
        if (order.getOrderNotes() != null && !order.getOrderNotes().isEmpty()) {
            html.append("<div style='background: #fff3cd; padding: 10px; border-radius: 5px; margin-top: 15px;'>");
            html.append("<strong>📝 Customer Notes:</strong><br>").append(order.getOrderNotes());
            html.append("</div>");
        }

        html.append("</div></body></html>");

        return html.toString();
    }
}
