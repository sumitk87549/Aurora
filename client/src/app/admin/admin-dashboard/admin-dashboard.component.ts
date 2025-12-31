import { Component, OnInit } from '@angular/core';
import { CandleService, Candle, CandleImage } from '../../services/candle.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule]
})
export class AdminDashboardComponent implements OnInit {
  candles: Candle[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  isCreating: boolean = false;
  selectedImages: File[] = [];

  newCandle: Candle = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    stockQuantity: 0,
    available: true,
    creatorsChoice: false,
    creatorsText: ''
  };

  isEditing: boolean = false;
  editingCandle: Candle | null = null;

  constructor(
    private candleService: CandleService,
    private authService: AuthService,
    private http: HttpClient
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
        next: (response) => {
          console.log('Upload response:', response);
          this.candles.push(this.newCandle);
          this.resetForm();
          alert('Candle created successfully with images!');
          this.isCreating = false;
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
        this.cancelEdit();
        alert('Candle updated successfully!');
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
      creatorsText: ''
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
}
