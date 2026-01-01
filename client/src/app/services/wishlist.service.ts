import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Candle, CandleImage } from './candle.service';

export interface WishlistItem {
  id: number;
  candle: Candle;
  addedAt: string;
}

export interface Wishlist {
  id: number;
  user: any;
  wishlistItems: WishlistItem[];
}

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private apiUrl = 'http://localhost:8081/api/wishlist';

  constructor(private http: HttpClient) { }

  getWishlist(): Observable<Wishlist> {
    return this.http.get<Wishlist>(this.apiUrl);
  }

  addToWishlist(candleId: number): Observable<Wishlist> {
    return this.http.post<Wishlist>(`${this.apiUrl}/add?candleId=${candleId}`, {});
  }

  removeFromWishlist(itemId: number): Observable<Wishlist> {
    return this.http.delete<Wishlist>(`${this.apiUrl}/remove/${itemId}`);
  }
}
