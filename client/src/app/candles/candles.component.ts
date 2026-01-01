import { Component, OnInit } from '@angular/core';
import { CandleService, Candle, CandleImage } from '../services/candle.service';
import { CartService } from '../services/cart.service';
import { WishlistService } from '../services/wishlist.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-candles',
  templateUrl: './candles.component.html',
  styleUrls: ['./candles.component.scss'],
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule]
})
export class CandlesComponent implements OnInit {
  candles: Candle[] = [];
  filteredCandles: Candle[] = [];
  paginatedCandles: Candle[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // Filter and Sort properties
  searchQuery: string = '';
  sortBy: string = 'name-asc';

  // Pagination properties
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 1;

  constructor(
    private candleService: CandleService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCandles();
  }

  loadCandles(): void {
    this.isLoading = true;
    this.candleService.getAllCandles().subscribe({
      next: (data) => {
        this.candles = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load candles';
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    // 1. Filter
    let tempCandles = this.candles;

    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase().trim();
      tempCandles = tempCandles.filter(candle =>
        candle.name.toLowerCase().includes(query) ||
        candle.description.toLowerCase().includes(query)
      );
    }

    // 2. Sort
    tempCandles = this.sortCandles(tempCandles);

    this.filteredCandles = tempCandles;

    // 3. Paginate
    this.totalPages = Math.ceil(this.filteredCandles.length / this.itemsPerPage);
    if (this.totalPages === 0) this.totalPages = 1;
    this.updatePagination();
  }

  sortCandles(candles: Candle[]): Candle[] {
    return candles.sort((a, b) => {
      switch (this.sortBy) {
        case 'price-asc':
          return a.price - b.price;
        case 'price-desc':
          return b.price - a.price;
        case 'name-asc':
          return a.name.localeCompare(b.name);
        case 'name-desc':
          return b.name.localeCompare(a.name);
        case 'newest':
          return b.id - a.id; // Assuming higher ID means newer
        default:
          return 0;
      }
    });
  }

  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedCandles = this.filteredCandles.slice(startIndex, endIndex);
  }

  onPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
      window.scrollTo(0, 0);
    }
  }

  getPages(): number[] {
    return Array(this.totalPages).fill(0).map((x, i) => i + 1);
  }

  // Carousel Logic
  imageIndices: { [key: number]: number } = {};

  nextImage(candle: Candle, event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    if (!candle.images || candle.images.length <= 1) return;

    const currentIndex = this.imageIndices[candle.id] || 0;
    this.imageIndices[candle.id] = (currentIndex + 1) % candle.images.length;
  }

  prevImage(candle: Candle, event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    if (!candle.images || candle.images.length <= 1) return;

    const currentIndex = this.imageIndices[candle.id] || 0;
    this.imageIndices[candle.id] = (currentIndex - 1 + candle.images.length) % candle.images.length;
  }

  getCurrentImageUrl(candle: Candle): string {
    if (!candle.images || candle.images.length === 0) {
      return '/assets/default-candle.jpg';
    }

    const index = this.imageIndices[candle.id] || 0;
    const image = candle.images[index];
    return this.getImageUrl(image);
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
        alert('Failed to add to cart: ' + error.message);
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

  getImageUrl(image?: CandleImage): string {
    if (!image || !image.id) {
      return '/assets/default-candle.jpg';
    }

    // If image has base64 data, use it directly
    if (image.imageData) {
      return image.imageData;
    }

    // Fallback to API endpoint if no base64 data
    return `http://localhost:8081/api/candles/images/${image.id}`;
  }

  navigateToCandleDetail(candle: Candle): void {
    this.router.navigate(['/candles', candle.id]);
  }
}
