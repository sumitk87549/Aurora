import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CartService, Cart } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

declare var Razorpay: any;

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class CheckoutComponent implements OnInit {
  cart: Cart | null = null;
  checkoutForm: FormGroup;
  isLoading: boolean = true;
  isProcessing: boolean = false;
  errorMessage: string = '';
  razorpayConfigured = false;
  razorpayKeyId = '';

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {
    this.checkoutForm = this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(2)]],
      customerEmail: ['', [Validators.required, Validators.email]],
      customerPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      shippingAddress: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pincode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
      paymentMethod: ['COD', Validators.required],
      orderNotes: ['']
    });
  }

  ngOnInit(): void {
    this.loadCart();
    this.loadPaymentConfig();
    this.loadRazorpayScript();
  }

  loadCart(): void {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cart = data;
        this.isLoading = false;
        if (!data || !data.cartItems || data.cartItems.length === 0) {
          this.router.navigate(['/cart']);
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to load cart';
        this.isLoading = false;
      }
    });
  }

  loadPaymentConfig(): void {
    this.orderService.getPaymentConfig().subscribe(config => {
      this.razorpayConfigured = config.razorpayConfigured;
      this.razorpayKeyId = config.razorpayKeyId;
    });
  }

  loadRazorpayScript(): void {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    document.body.appendChild(script);
  }

  getTotalPrice(): number {
    if (!this.cart) return 0;
    return this.cart.cartItems.reduce((total, item) =>
      total + (item.priceAtTime * item.quantity), 0);
  }

  getShippingCost(): number {
    const total = this.getTotalPrice();
    return total >= 999 ? 0 : 99;
  }

  getGrandTotal(): number {
    return this.getTotalPrice() + this.getShippingCost();
  }

  onSubmit(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      return;
    }

    this.isProcessing = true;
    const formValue = this.checkoutForm.value;
    const request = {
      ...formValue,
      shippingAddress: `${formValue.shippingAddress}, ${formValue.city}, ${formValue.state} - ${formValue.pincode}`
    };

    if (this.checkoutForm.get('paymentMethod')?.value === 'COD') {
      this.processCODOrder(request);
    } else {
      this.processRazorpayOrder(request);
    }
  }

  processCODOrder(request: any): void {
    this.orderService.checkoutCOD(request).subscribe({
      next: (order) => {
        alert('Order placed successfully! Order #' + order.orderNumber);
        this.router.navigate(['/orders', order.id]); // Or order confirmation page
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to place order';
        this.isProcessing = false;
      }
    });
  }

  processRazorpayOrder(request: any): void {
    this.orderService.createRazorpayOrder(request).subscribe({
      next: (response) => {
        this.openRazorpayCheckout(response);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to initiate payment';
        this.isProcessing = false;
      }
    });
  }

  openRazorpayCheckout(orderData: any): void {
    const options = {
      key: this.razorpayKeyId,
      amount: orderData.amount,
      currency: orderData.currency,
      name: 'Aurora Flames',
      description: 'Order #' + orderData.orderNumber,
      order_id: orderData.razorpayOrderId,
      handler: (response: any) => {
        this.verifyPayment(response, orderData.orderNumber);
      },
      prefill: {
        name: orderData.customerName,
        email: orderData.customerEmail,
        contact: orderData.customerPhone
      },
      theme: {
        color: '#C4956A'
      }
    };

    if (typeof Razorpay !== 'undefined') {
      const rzp = new Razorpay(options);
      rzp.on('payment.failed', (response: any) => {
        alert('Payment Failed: ' + response.error.description);
        this.isProcessing = false;
      });
      rzp.open();
    } else {
      alert('Razorpay SDK not loaded. Please check your internet connection.');
      this.isProcessing = false;
    }
  }

  verifyPayment(paymentResponse: any, orderNumber: string): void {
    const paymentData = {
      razorpay_order_id: paymentResponse.razorpay_order_id,
      razorpay_payment_id: paymentResponse.razorpay_payment_id,
      razorpay_signature: paymentResponse.razorpay_signature,
      orderNumber: orderNumber
    };

    this.orderService.verifyRazorpayPayment(paymentData).subscribe({
      next: (order) => {
        alert('Payment Successful! Order Placed.');
        this.router.navigate(['/orders', order.id]);
      },
      error: (error) => {
        alert('Payment verification failed. Please contact support.');
        this.isProcessing = false;
      }
    });
  }
}

