import { Component, OnInit } from '@angular/core';
import { CartService, Cart } from '../services/cart.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

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
      },
      error: (error) => {
        this.errorMessage = 'Failed to load cart';
        this.isLoading = false;
      }
    });
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(itemId);
      return;
    }

    this.cartService.updateCartItem(itemId, quantity).subscribe({
      next: (updatedCart) => {
        this.cart = updatedCart;
      },
      error: (error) => {
        alert('Failed to update quantity');
      }
    });
  }

  removeItem(itemId: number): void {
    this.cartService.removeFromCart(itemId).subscribe({
      next: (updatedCart) => {
        this.cart = updatedCart;
      },
      error: (error) => {
        alert('Failed to remove item');
      }
    });
  }

  clearCart(): void {
    if (confirm('Are you sure you want to clear your cart?')) {
      this.cartService.clearCart().subscribe({
        next: () => {
          this.cart = { ...this.cart!, cartItems: [] };
        },
        error: (error) => {
          alert('Failed to clear cart');
        }
      });
    }
  }

  getTotalPrice(): number {
    if (!this.cart) return 0;
    return this.cart.cartItems.reduce((total, item) => 
      total + (item.priceAtTime * item.quantity), 0);
  }

  getTotalItems(): number {
    if (!this.cart) return 0;
    return this.cart.cartItems.reduce((total, item) => total + item.quantity, 0);
  }

  proceedToCheckout(): void {
    this.router.navigate(['/checkout']);
  }
}
