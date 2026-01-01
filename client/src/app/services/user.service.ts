import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { AuthService } from './auth.service';

export interface UserProfile {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    profileEmoji: string;
    defaultAddress: string;
    city: string;
    state: string;
    pincode: string;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8081/api/users';
    private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
    userProfile$ = this.userProfileSubject.asObservable();

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    getProfile(): Observable<UserProfile> {
        const token = this.authService.getToken();
        return this.http.get<UserProfile>(`${this.apiUrl}/profile`, {
            headers: { Authorization: `Bearer ${token}` }
        }).pipe(
            tap(profile => this.userProfileSubject.next(profile))
        );
    }

    updateProfile(profile: UserProfile): Observable<UserProfile> {
        const token = this.authService.getToken();
        return this.http.put<UserProfile>(`${this.apiUrl}/profile`, profile, {
            headers: { Authorization: `Bearer ${token}` }
        }).pipe(
            tap(updatedProfile => this.userProfileSubject.next(updatedProfile))
        );
    }

    refreshProfile(): void {
        if (this.authService.isLoggedIn()) {
            this.getProfile().subscribe();
        } else {
            this.userProfileSubject.next(null);
        }
    }
}
