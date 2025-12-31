import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Candle {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  available: boolean;
  images?: CandleImage[];
}

export interface CandleImage {
  id: number;
  imageUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class CandleService {
  private apiUrl = 'http://localhost:8080/api/candles';
  private adminApiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) { }

  getAllCandles(): Observable<Candle[]> {
    return this.http.get<Candle[]>(this.apiUrl);
  }

  getCandleById(id: number): Observable<Candle> {
    return this.http.get<Candle>(`${this.apiUrl}/${id}`);
  }

  searchCandles(name: string): Observable<Candle[]> {
    return this.http.get<Candle[]>(`${this.apiUrl}/search?name=${name}`);
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('currentUser');
    const user = token ? JSON.parse(token) : null;
    return new HttpHeaders({
      'Authorization': `Bearer ${user?.token}`
    });
  }

  createCandle(candle: Candle): Observable<Candle> {
    return this.http.post<Candle>(`${this.adminApiUrl}/candles`, candle, 
      { headers: this.getAuthHeaders() });
  }

  updateCandle(id: number, candle: Candle): Observable<Candle> {
    return this.http.put<Candle>(`${this.adminApiUrl}/candles/${id}`, candle, 
      { headers: this.getAuthHeaders() });
  }

  deleteCandle(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApiUrl}/candles/${id}`, 
      { headers: this.getAuthHeaders() });
  }
}
