import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgIf, NgFor, CurrencyPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { CandleService, Candle, CandleImage } from '../../services/candle.service';
import { CartService, Cart, CartItem } from '../../services/cart.service';
import { WishlistService } from '../../services/wishlist.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { Subscription } from 'rxjs';
import { API_URL } from '../../config/api.config';

@Component({
  selector: 'app-candle-detail',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, RouterLink, CurrencyPipe],
  templateUrl: './candle-detail.component.html',
  styleUrl: './candle-detail.component.scss'
})
export class CandleDetailComponent implements OnInit, OnDestroy {
  candle: Candle | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  currentImageIndex: number = 0;

  // Cart state
  cartItem: CartItem | null = null;
  isUpdatingCart: boolean = false;

  // Wishlist state
  isInWishlist: boolean = false;
  isUpdatingWishlist: boolean = false;
  private subscriptions: Subscription[] = [];

  // Image zoom state
  isZooming: boolean = false;
  zoomX: number = 0;
  zoomY: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private candleService: CandleService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private authService: AuthService,
    private toastService: ToastService
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
        this.checkWishlistStatus();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load candle parameters';
        this.isLoading = false;
      }
    });
  }

  checkCartStatus(): void {
    if (this.isLoggedIn() && this.candle) {
      const sub = this.cartService.getCart().subscribe({
        next: (cart) => {
          this.cartItem = cart.cartItems.find(item => item.candle.id === this.candle?.id) || null;
        }
      });
      this.subscriptions.push(sub);
    }
  }

  checkWishlistStatus(): void {
    if (this.isLoggedIn() && this.candle) {
      const sub = this.wishlistService.getWishlist().subscribe({
        next: (wishlist) => {
          this.isInWishlist = wishlist.wishlistItems.some(item => item.candle.id === this.candle?.id);
        }
      });
      this.subscriptions.push(sub);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
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

    // Priority: imageUrl > imageData > API endpoint
    if (image.imageUrl) {
      return image.imageUrl;
    }

    if (image.imageData) {
      return image.imageData;
    }

    return `${API_URL}/candles/images/${image.id}`;
  }

  handleImageError(event: any): void {
    event.target.src = '/assets/default-candle.jpg';
    // Prevent infinite loop if default image also fails
    event.target.onerror = null;
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
        this.toastService.success('Added to cart!');
      },
      error: (error) => {
        this.toastService.error('Failed to add to cart');
        this.isUpdatingCart = false;
      }
    });
  }

  updateQuantity(delta: number): void {
    console.log('updateQuantity called with delta:', delta);
    console.log('Current cartItem:', this.cartItem);
    console.log('Current candle stock:', this.candle?.stockQuantity);

    if (!this.checkLogin() || !this.cartItem) return;

    const newQuantity = this.cartItem.quantity + delta;
    console.log('New quantity would be:', newQuantity);

    this.isUpdatingCart = true;

    if (newQuantity <= 0) {
      this.cartService.removeFromCart(this.cartItem.id).subscribe({
        next: (cart) => {
          this.cartItem = null;
          this.isUpdatingCart = false;
          this.toastService.info('Removed from cart');
        },
        error: (error) => {
          this.toastService.error('Failed to remove item');
          this.isUpdatingCart = false;
        }
      });
    } else {
      // Check stock limit if increasing
      if (delta > 0 && this.candle && newQuantity > this.candle.stockQuantity) {
        this.toastService.warning(`Sorry, only ${this.candle.stockQuantity} items available in stock.`);
        this.isUpdatingCart = false;
        return;
      }

      this.cartService.updateCartItem(this.cartItem.id, newQuantity).subscribe({
        next: (cart) => {
          this.cartItem = cart.cartItems.find(item => item.candle.id === this.candle?.id) || null;
          this.isUpdatingCart = false;
        },
        error: (error) => {
          this.toastService.error('Failed to update quantity');
          this.isUpdatingCart = false;
        }
      });
    }
  }

  toggleWishlist(): void {
    if (!this.checkLogin() || !this.candle) return;

    this.isUpdatingWishlist = true;

    if (this.isInWishlist) {
      this.wishlistService.getWishlist().subscribe({
        next: (wishlist) => {
          const item = wishlist.wishlistItems.find(i => i.candle.id === this.candle?.id);
          if (item) {
            this.wishlistService.removeFromWishlist(item.id).subscribe({
              next: () => {
                this.isInWishlist = false;
                this.toastService.info('Removed from wishlist');
                this.isUpdatingWishlist = false;
              },
              error: () => {
                this.toastService.error('Failed to remove from wishlist');
                this.isUpdatingWishlist = false;
              }
            });
          }
        }
      });
    } else {
      this.wishlistService.addToWishlist(this.candle.id).subscribe({
        next: () => {
          this.isInWishlist = true;
          this.toastService.success('Added to wishlist!');
          this.isUpdatingWishlist = false;
        },
        error: (error) => {
          this.toastService.error('Failed to add to wishlist');
          this.isUpdatingWishlist = false;
        }
      });
    }
  }

  checkLogin(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.toastService.warning('Please login to perform this action');
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  goBack(): void {
    this.location.back();
  }

  // Image zoom functionality
  onImageMouseMove(event: MouseEvent): void {
    const target = event.currentTarget as HTMLElement;
    const rect = target.getBoundingClientRect();

    // Calculate position as percentage
    this.zoomX = ((event.clientX - rect.left) / rect.width) * 100;
    this.zoomY = ((event.clientY - rect.top) / rect.height) * 100;
  }

  onImageMouseEnter(): void {
    this.isZooming = true;
  }

  onImageMouseLeave(): void {
    this.isZooming = false;
  }

  // Touch support for mobile
  onImageTouchStart(event: TouchEvent): void {
    this.isZooming = true;
    this.updateZoomPosition(event);
  }

  onImageTouchMove(event: TouchEvent): void {
    if (this.isZooming) {
      this.updateZoomPosition(event);
    }
  }

  onImageTouchEnd(): void {
    this.isZooming = false;
  }

  private updateZoomPosition(event: TouchEvent): void {
    const target = event.currentTarget as HTMLElement;
    const rect = target.getBoundingClientRect();
    const touch = event.touches[0];

    if (touch) {
      this.zoomX = ((touch.clientX - rect.left) / rect.width) * 100;
      this.zoomY = ((touch.clientY - rect.top) / rect.height) * 100;
    }
  }
}
