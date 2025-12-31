import { Component, OnInit } from '@angular/core';
import { CandleService, Candle } from '../services/candle.service';
import { CartService } from '../services/cart.service';
import { WishlistService } from '../services/wishlist.service';
import { AuthService } from '../services/auth.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';

@Component({
  selector: 'app-candles',
  templateUrl: './candles.component.html',
  styleUrls: ['./candles.component.scss'],
  standalone: true,
  imports: [CommonModule, NgIf, NgFor]
})
export class CandlesComponent implements OnInit {
  candles: Candle[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private candleService: CandleService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCandles();
  }

  loadCandles(): void {
    this.isLoading = true;
    this.candleService.getAllCandles().subscribe({
      next: (data) => {
        this.candles = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load candles';
        this.isLoading = false;
      }
    });
  }

  addToCart(candle: Candle): void {
    if (!this.authService.isLoggedIn()) {
      alert('Please login to add items to cart');
      return;
    }

    this.cartService.addToCart(candle.id, 1).subscribe({
      next: () => {
        alert('Added to cart successfully!');
      },
      error: (error) => {
        alert('Failed to add to cart');
      }
    });
  }

  addToWishlist(candle: Candle): void {
    if (!this.authService.isLoggedIn()) {
      alert('Please login to add items to wishlist');
      return;
    }

    this.wishlistService.addToWishlist(candle.id).subscribe({
      next: () => {
        alert('Added to wishlist successfully!');
      },
      error: (error) => {
        alert('Failed to add to wishlist');
      }
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
