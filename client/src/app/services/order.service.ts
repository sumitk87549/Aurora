import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Order {
    id: number;
    orderNumber: string;
    totalAmount: number;
    subtotal: number;
    shippingCost: number;
    discountAmount: number;
    status: string;
    paymentMethod: string;
    paymentStatus: string;
    shippingAddress: string;
    customerName: string;
    customerPhone: string;
    customerEmail: string;
    orderDate: string;
    expectedDeliveryDate: string;
    deliveryDate?: string;
    orderItems: OrderItem[];
}

export interface OrderItem {
    id: number;
    candle: {
        id: number;
        name: string;
        price: number;
    };
    quantity: number;
    priceAtTime: number;
}

export interface CheckoutRequest {
    shippingAddress: string;
    paymentMethod: string;
    customerName: string;
    customerPhone: string;
    customerEmail?: string;
    city?: string;
    state?: string;
    pincode?: string;
    orderNotes?: string;
}

export interface RazorpayOrderResponse {
    razorpayOrderId: string;
    amount: number;
    currency: string;
    razorpayKeyId: string;
    orderNumber: string;
    customerName: string;
    customerEmail: string;
    customerPhone: string;
}

export interface PaymentConfig {
    razorpayConfigured: boolean;
    razorpayKeyId: string;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = 'http://localhost:8081/api/orders';

    constructor(private http: HttpClient) { }

    // Get payment configuration
    getPaymentConfig(): Observable<PaymentConfig> {
        return this.http.get<PaymentConfig>(`${this.apiUrl}/payment/config`);
    }

    // Checkout with COD
    checkoutCOD(request: CheckoutRequest): Observable<Order> {
        return this.http.post<Order>(`${this.apiUrl}/checkout/cod`, request);
    }

    // Create Razorpay order
    createRazorpayOrder(request: CheckoutRequest): Observable<RazorpayOrderResponse> {
        return this.http.post<RazorpayOrderResponse>(`${this.apiUrl}/checkout/razorpay/create`, request);
    }

    // Verify Razorpay payment
    verifyRazorpayPayment(paymentData: any): Observable<Order> {
        return this.http.post<Order>(`${this.apiUrl}/checkout/razorpay/verify`, paymentData);
    }

    // Get user's orders
    getUserOrders(): Observable<Order[]> {
        return this.http.get<Order[]>(this.apiUrl);
    }

    // Get order by ID
    getOrderById(id: number): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/${id}`);
    }

    // Get order by order number
    getOrderByNumber(orderNumber: string): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/number/${orderNumber}`);
    }

    // ================== ADMIN METHODS ==================

    getAllOrders(): Observable<Order[]> {
        return this.http.get<Order[]>('http://localhost:8081/api/admin/orders');
    }

    updateOrderStatus(id: number, status: string): Observable<Order> {
        return this.http.put<Order>(`http://localhost:8081/api/admin/orders/${id}/status?status=${status}`, {});
    }

    getDashboardStats(): Observable<any> {
        return this.http.get<any>('http://localhost:8081/api/admin/stats');
    }
}
