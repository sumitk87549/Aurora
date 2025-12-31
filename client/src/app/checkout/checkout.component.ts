import { Component, OnInit } from '@angular/core';
import { CartService, Cart } from '../services/cart.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss'],
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class CheckoutComponent implements OnInit {
  cart: Cart | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  
  checkoutData = {
    shippingAddress: '',
    paymentMethod: 'COD'
  };

  constructor(private cartService: CartService, private router: Router) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cart = data;
        this.isLoading = false;
        if (!data.cartItems || data.cartItems.length === 0) {
          this.router.navigate(['/cart']);
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to load cart';
        this.isLoading = false;
      }
    });
  }

  getTotalPrice(): number {
    if (!this.cart) return 0;
    return this.cart.cartItems.reduce((total, item) => 
      total + (item.priceAtTime * item.quantity), 0);
  }

  placeOrder(): void {
    if (!this.checkoutData.shippingAddress.trim()) {
      alert('Please enter a shipping address');
      return;
    }

    // This would normally call an order service
    alert('Order placed successfully! (COD payment)');
    this.router.navigate(['/']);
  }
}
