import { Component, OnInit } from '@angular/core';
import { CandleService, Candle } from '../../services/candle.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  createCandle(): void {
    this.candleService.createCandle(this.newCandle).subscribe({
      next: (createdCandle) => {
        this.candles.push(createdCandle);
        this.resetForm();
        alert('Candle created successfully!');
      },
      error: (error) => {
        alert('Failed to create candle');
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
}
