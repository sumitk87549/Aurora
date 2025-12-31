package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Order;
import com.ecomm.AuroraFlames.repository.OrderRepository;
import com.ecomm.AuroraFlames.service.EmailService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        try {
            // 1. Verify Signature
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
            }

            // 2. Parse Payload
            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");
            JSONObject payloadEntity = json.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            if ("payment.captured".equals(event) || "order.paid".equals(event)) {
                String razorpayOrderId = payloadEntity.getString("order_id");
                String razorpayPaymentId = payloadEntity.getString("id"); // Payment ID

                // 3. Find Order
                Optional<Order> orderOpt = orderRepository.findByRazorpayOrderId(razorpayOrderId);

                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();

                    // 4. Update Status if not already paid
                    if (!"PAID".equals(order.getPaymentStatus())) {
                        order.setPaymentStatus("PAID");
                        order.setStatus("CONFIRMED"); // Auto-confirm on payment
                        order.setRazorpayPaymentId(razorpayPaymentId);
                        orderRepository.save(order);

                        // 5. Send Emails
                        emailService.sendOrderConfirmationToCustomer(order);
                        emailService.sendOrderNotificationToAdmin(order);
                    }
                }
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
}
