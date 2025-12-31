package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.dto.RazorpayOrderResponse;
import com.ecomm.AuroraFlames.entity.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Formatter;

@Service
public class PaymentService {

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    /**
     * Creates a Razorpay order for the given amount
     */
    public RazorpayOrderResponse createRazorpayOrder(Order order) throws RazorpayException {
        if (razorpayKeyId == null || razorpayKeyId.isEmpty() ||
                razorpayKeyId.equals("YOUR_RAZORPAY_KEY_ID")) {
            throw new RuntimeException(
                    "Razorpay is not configured. Please add your Razorpay credentials to application.properties");
        }

        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Amount should be in paise (1 INR = 100 paise)
        int amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", order.getOrderNumber());
        orderRequest.put("payment_capture", 1); // Auto capture

        // Add notes
        JSONObject notes = new JSONObject();
        notes.put("order_number", order.getOrderNumber());
        notes.put("customer_name", order.getCustomerName());
        orderRequest.put("notes", notes);

        com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setRazorpayOrderId(razorpayOrder.get("id"));
        response.setAmount(order.getTotalAmount());
        response.setCurrency("INR");
        response.setRazorpayKeyId(razorpayKeyId);
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setCustomerPhone(order.getCustomerPhone());

        return response;
    }

    /**
     * Verifies the Razorpay payment signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = calculateHmacSha256(payload, razorpayKeySecret);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            System.err.println("Error verifying payment signature: " + e.getMessage());
            return false;
        }
    }

    private String calculateHmacSha256(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmac = mac.doFinal(data.getBytes());
        return toHexString(hmac);
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * Check if Razorpay is properly configured
     */
    public boolean isRazorpayConfigured() {
        return razorpayKeyId != null && !razorpayKeyId.isEmpty() &&
                !razorpayKeyId.equals("YOUR_RAZORPAY_KEY_ID") &&
                razorpayKeySecret != null && !razorpayKeySecret.isEmpty() &&
                !razorpayKeySecret.equals("YOUR_RAZORPAY_KEY_SECRET");
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
}
