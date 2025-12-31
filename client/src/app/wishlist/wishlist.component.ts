import { Component, OnInit } from '@angular/core';
import { WishlistService, Wishlist } from '../services/wishlist.service';
import { CartService } from '../services/cart.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';

@Component({
  selector: 'app-wishlist',
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.scss'],
  standalone: true,
  imports: [CommonModule, NgIf, NgFor]
})
export class WishlistComponent implements OnInit {
  wishlist: Wishlist | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private wishlistService: WishlistService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.loadWishlist();
  }

  loadWishlist(): void {
    this.isLoading = true;
    this.wishlistService.getWishlist().subscribe({
      next: (data) => {
        this.wishlist = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load wishlist';
        this.isLoading = false;
      }
    });
  }

  removeFromWishlist(itemId: number): void {
    this.wishlistService.removeFromWishlist(itemId).subscribe({
      next: (updatedWishlist) => {
        this.wishlist = updatedWishlist;
      },
      error: (error) => {
        alert('Failed to remove from wishlist');
      }
    });
  }

  addToCart(candleId: number): void {
    this.cartService.addToCart(candleId, 1).subscribe({
      next: () => {
        alert('Added to cart successfully!');
      },
      error: (error) => {
        alert('Failed to add to cart');
      }
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
