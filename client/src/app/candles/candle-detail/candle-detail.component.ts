import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgFor, CurrencyPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CandleService, Candle, CandleImage } from '../../services/candle.service';
import { CartService, Cart, CartItem } from '../../services/cart.service';
import { WishlistService } from '../../services/wishlist.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-candle-detail',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, RouterLink, CurrencyPipe],
  templateUrl: './candle-detail.component.html',
  styleUrl: './candle-detail.component.scss'
})
export class CandleDetailComponent implements OnInit {
  candle: Candle | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  currentImageIndex: number = 0;

  // Cart state
  cartItem: CartItem | null = null;
  isUpdatingCart: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private candleService: CandleService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadCandle(id);
      }
    });
  }

  loadCandle(id: number): void {
    this.isLoading = true;
    this.candleService.getCandleById(id).subscribe({
      next: (data) => {
        this.candle = data;
        this.isLoading = false;
        this.checkCartStatus();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load candle parameters';
        this.isLoading = false;
      }
    });
  }

  checkCartStatus(): void {
    if (this.isLoggedIn() && this.candle) {
      this.cartService.getCart().subscribe({
        next: (cart) => {
          this.cartItem = cart.cartItems.find(item => item.candle.id === this.candle?.id) || null;
        }
      });
    }
  }

  // Carousel Logic
  nextImage(): void {
    if (!this.candle?.images || this.candle.images.length <= 1) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % this.candle.images.length;
  }

  prevImage(): void {
    if (!this.candle?.images || this.candle.images.length <= 1) return;
    this.currentImageIndex = (this.currentImageIndex - 1 + this.candle.images.length) % this.candle.images.length;
  }

  getCurrentImageUrl(): string {
    if (!this.candle?.images || this.candle.images.length === 0) {
      return '/assets/default-candle.jpg';
    }
    const image = this.candle.images[this.currentImageIndex];
    return this.getImageUrl(image);
  }

  getImageUrl(image?: CandleImage): string {
    if (!image || !image.id) {
      return '/assets/default-candle.jpg';
    }

    if (image.imageData) {
      return image.imageData;
    }

    return `http://localhost:8081/api/candles/images/${image.id}`;
  }

  // Cart Logic
  addToCart(): void {
    if (!this.checkLogin()) return;
    if (!this.candle) return;

    this.isUpdatingCart = true;
    this.cartService.addToCart(this.candle.id, 1).subscribe({
      next: (cart) => {
        this.cartItem = cart.cartItems.find(item => item.candle.id === this.candle?.id) || null;
        this.isUpdatingCart = false;
        // alert('Added to cart!'); 
      },
      error: (error) => {
        alert('Failed to add to cart');
        this.isUpdatingCart = false;
      }
    });
  }

  updateQuantity(delta: number): void {
    if (!this.checkLogin() || !this.cartItem) return;

    const newQuantity = this.cartItem.quantity + delta;
    this.isUpdatingCart = true;

    if (newQuantity <= 0) {
      this.cartService.removeFromCart(this.cartItem.id).subscribe({
        next: (cart) => {
          this.cartItem = null;
          this.isUpdatingCart = false;
        },
        error: (error) => {
          alert('Failed to remove item');
          this.isUpdatingCart = false;
        }
      });
    } else {
      // Check stock limit if increasing
      if (delta > 0 && this.candle && newQuantity > this.candle.stockQuantity) {
        alert(`Sorry, only ${this.candle.stockQuantity} items available in stock.`);
        this.isUpdatingCart = false;
        return;
      }

      this.cartService.updateCartItem(this.cartItem.id, newQuantity).subscribe({
        next: (cart) => {
          this.cartItem = cart.cartItems.find(item => item.candle.id === this.candle?.id) || null;
          this.isUpdatingCart = false;
        },
        error: (error) => {
          alert('Failed to update quantity');
          this.isUpdatingCart = false;
        }
      });
    }
  }

  addToWishlist(): void {
    if (!this.checkLogin() || !this.candle) return;

    this.wishlistService.addToWishlist(this.candle.id).subscribe({
      next: () => {
        alert('Added to wishlist successfully!');
      },
      error: (error) => {
        alert('Failed to add to wishlist');
      }
    });
  }

  checkLogin(): boolean {
    if (!this.authService.isLoggedIn()) {
      alert('Please login to perform this action');
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
