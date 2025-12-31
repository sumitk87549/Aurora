import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WishlistItem {
  id: number;
  candle: Candle;
  addedAt: string;
}

export interface Candle {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  available: boolean;
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
  private apiUrl = 'http://localhost:8080/api/wishlist';

  constructor(private http: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('currentUser');
    const user = token ? JSON.parse(token) : null;
    return new HttpHeaders({
      'Authorization': `Bearer ${user?.token}`
    });
  }

  getWishlist(): Observable<Wishlist> {
    return this.http.get<Wishlist>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  addToWishlist(candleId: number): Observable<Wishlist> {
    return this.http.post<Wishlist>(`${this.apiUrl}/add?candleId=${candleId}`, {}, 
      { headers: this.getAuthHeaders() });
  }

  removeFromWishlist(itemId: number): Observable<Wishlist> {
    return this.http.delete<Wishlist>(`${this.apiUrl}/remove/${itemId}`, 
      { headers: this.getAuthHeaders() });
  }
}
