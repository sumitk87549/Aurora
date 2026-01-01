import { Component, OnInit } from '@angular/core';
import { CandleService, Candle, CandleImage } from '../../services/candle.service';
import { AuthService } from '../../services/auth.service';
import { OrderService, Order } from '../../services/order.service';
import { ToastService } from '../../services/toast.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

interface DashboardStats {
  totalRevenue: number;
  todayRevenue: number;
  totalOrders: number;
  pendingOrders: number;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule]
})
export class AdminDashboardComponent implements OnInit {
  candles: Candle[] = [];
  orders: Order[] = [];
  stats: DashboardStats = {
    totalRevenue: 0,
    todayRevenue: 0,
    totalOrders: 0,
    pendingOrders: 0
  };
  activeTab: 'dashboard' | 'orders' | 'products' = 'dashboard';
  isLoading: boolean = true;
  errorMessage: string = '';
  isCreating: boolean = false;
  isUploading: boolean = false;
  selectedImages: File[] = [];
  editingImages: File[] = [];
  currentImageIndex: { [key: number]: number } = {};

  newCandle: Candle = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    stockQuantity: 0,
    available: true,
    creatorsChoice: false,
    creatorsText: '',
    featured: false
  };

  isEditing: boolean = false;
  editingCandle: Candle | null = null;

  constructor(
    private candleService: CandleService,
    private orderService: OrderService,
    private authService: AuthService,
    private toastService: ToastService,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCandles();
    this.loadOrders();
    this.loadDashboardStats();
  }

  setActiveTab(tab: 'dashboard' | 'orders' | 'products'): void {
    this.activeTab = tab;
  }

  loadDashboardStats(): void {
    this.isLoading = true;
    this.orderService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.isLoading = false;
        // Fallback to default values if API fails
        this.stats = {
          totalRevenue: 0,
          todayRevenue: 0,
          totalOrders: 0,
          pendingOrders: 0
        };
      }
    });
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (orders: Order[]) => {
        this.orders = orders;
        this.isLoading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load orders';
        this.isLoading = false;
        console.error('Error loading orders:', error);
        // Provide some mock data for development
        this.orders = [];
      }
    });
  }

  viewOrderDetails(orderId: number): void {
    this.router.navigate(['/admin/orders', orderId]);
  }

  updateOrderStatus(orderId: number, event: Event): void {
    const target = event.target as HTMLSelectElement;
    const newStatus = target.value;
    const previousStatus = this.orders.find(o => o.id === orderId)?.status || '';

    this.orderService.updateOrderStatus(orderId, newStatus).subscribe({
      next: (updatedOrder) => {
        const index = this.orders.findIndex(o => o.id === orderId);
        if (index !== -1) {
          this.orders[index] = { ...this.orders[index], status: newStatus };
        }
      },
      error: (error) => {
        console.error('Error updating order status:', error);
        alert('Failed to update order status');
        // Reset to previous value on error
        target.value = previousStatus;
      }
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/assets/default-candle.jpg';
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

  createCandle(): void {
    this.isCreating = true;

    // First create the candle
    this.candleService.createCandle(this.newCandle).subscribe({
      next: (createdCandle) => {
        // Then upload images if any
        if (this.selectedImages.length > 0) {
          this.uploadImages(createdCandle.id, this.selectedImages);
        } else {
          this.candles.push(createdCandle);
          this.resetForm();
          alert('Candle created successfully!');
          this.isCreating = false;
        }
      },
      error: (error) => {
        alert('Failed to create candle');
        this.isCreating = false;
      }
    });
  }

  onImageSelect(event: any): void {
    this.selectedImages = Array.from(event.target.files);
  }

  onEditingImageSelect(event: any): void {
    this.editingImages = Array.from(event.target.files);
  }

  uploadImages(candleId: number, images: File[]): void {
    const formData = new FormData();
    images.forEach((image, index) => {
      formData.append('files', image);
    });

    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    console.log('Uploading images for candle ID:', candleId);
    console.log('Number of images:', images.length);
    console.log('Token:', token ? 'Present' : 'Missing');

    this.http.post(`http://localhost:8081/api/admin/candles/${candleId}/images`, formData, { headers })
      .subscribe({
        next: (response: any) => {
          console.log('Upload response:', response);
          if (response.success) {
            this.candles.push(this.newCandle);
            this.resetForm();
            alert(`Candle created successfully! ${response.message}`);
            this.isCreating = false;
          } else {
            alert('Candle created but image upload failed: ' + response.message);
            this.isCreating = false;
          }
        },
        error: (error) => {
          console.error('Upload error:', error);
          alert('Candle created but image upload failed: ' + (error.message || 'Unknown error'));
          this.isCreating = false;
        }
      });
  }

  updateCandle(): void {
    if (!this.editingCandle) return;

    this.candleService.updateCandle(this.editingCandle.id, this.editingCandle).subscribe({
      next: (updatedCandle) => {
        const index = this.candles.findIndex(c => c.id === updatedCandle.id);
        if (index !== -1) {
          this.candles[index] = updatedCandle;
        }

        // Upload new images if any
        if (this.editingImages.length > 0) {
          this.uploadImages(updatedCandle.id, this.editingImages);
        } else {
          this.cancelEdit();
          alert('Candle updated successfully!');
        }
      },
      error: (error) => {
        alert('Failed to update candle');
      }
    });
  }

  deleteCandle(id: number): void {
    if (confirm('Are you sure you want to delete this candle?')) {
      this.candleService.deleteCandle(id).subscribe({
        next: () => {
          this.candles = this.candles.filter(c => c.id !== id);
          alert('Candle deleted successfully!');
        },
        error: (error) => {
          alert('Failed to delete candle');
        }
      });
    }
  }

  editCandle(candle: Candle): void {
    this.isEditing = true;
    this.editingCandle = { ...candle };
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.editingCandle = null;
    this.editingImages = [];
  }

  resetForm(): void {
    this.newCandle = {
      id: 0,
      name: '',
      description: '',
      price: 0,
      stockQuantity: 0,
      available: true,
      creatorsChoice: false,
      creatorsText: '',
      featured: false
    };
    this.selectedImages = [];
  }

  onCreatorsChoiceChange(): void {
    if (!this.newCandle.creatorsChoice) {
      this.newCandle.creatorsText = '';
    }
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/';
  }

  getImageUrl(image?: CandleImage): string {
    if (!image || !image.id) {
      return '/assets/default-candle.jpg';
    }
    return `http://localhost:8081/api/candles/images/${image.id}`;
  }

  // Image navigation for product cards
  nextImage(candle: Candle, event: Event): void {
    event.stopPropagation();
    if (!candle.images || candle.images.length <= 1) return;

    const currentIndex = this.currentImageIndex[candle.id] || 0;
    this.currentImageIndex[candle.id] = (currentIndex + 1) % candle.images.length;
  }

  prevImage(candle: Candle, event: Event): void {
    event.stopPropagation();
    if (!candle.images || candle.images.length <= 1) return;

    const currentIndex = this.currentImageIndex[candle.id] || 0;
    this.currentImageIndex[candle.id] = (currentIndex - 1 + candle.images.length) % candle.images.length;
  }

  getCurrentImageUrl(candle: Candle): string {
    if (!candle.images || candle.images.length === 0) {
      return '/assets/default-candle.jpg';
    }

    const index = this.currentImageIndex[candle.id] || 0;
    const image = candle.images[index];
    return this.getImageUrl(image);
  }

  deleteImage(candleId: number, imageId: number): void {
    if (confirm('Are you sure you want to delete this image?')) {
      const token = this.authService.getToken();
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

      this.http.delete(`http://localhost:8081/api/admin/candles/${candleId}/images/${imageId}`, { headers })
        .subscribe({
          next: () => {
            // Refresh the candle data
            this.loadCandles();
            alert('Image deleted successfully!');
          },
          error: (error) => {
            alert('Failed to delete image');
          }
        });
    }
  }
}
