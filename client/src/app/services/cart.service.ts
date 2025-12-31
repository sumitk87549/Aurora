import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CartItem {
  id: number;
  candle: Candle;
  quantity: number;
  priceAtTime: number;
}

export interface Candle {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  available: boolean;
}

export interface Cart {
  id: number;
  user: any;
  cartItems: CartItem[];
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/cart';

  constructor(private http: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('currentUser');
    const user = token ? JSON.parse(token) : null;
    return new HttpHeaders({
      'Authorization': `Bearer ${user?.token}`
    });
  }

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  addToCart(candleId: number, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/add?candleId=${candleId}&quantity=${quantity}`, {}, 
      { headers: this.getAuthHeaders() });
  }

  updateCartItem(itemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/update/${itemId}?quantity=${quantity}`, {}, 
      { headers: this.getAuthHeaders() });
  }

  removeFromCart(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/remove/${itemId}`, 
      { headers: this.getAuthHeaders() });
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/clear`, 
      { headers: this.getAuthHeaders() });
  }
}
