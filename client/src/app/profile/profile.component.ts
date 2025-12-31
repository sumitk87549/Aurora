import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService, UserProfile } from '../services/user.service';
import { CommonModule, NgIf, NgFor } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIf, NgFor]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  isLoading: boolean = true;
  isSaving: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  emojis: string[] = ['🕯️', '✨', '🔥', '⭐', '🌙', '☀️', '🌸', '💀', '🎃', '🎄', '🤍', '🧡', '💜', '🪄', '🧚'];

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: [{ value: '', disabled: true }, Validators.required], // Email cannot be changed
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      profileEmoji: ['🕯️'],
      defaultAddress: [''],
      city: [''],
      state: [''],
      pincode: ['', Validators.pattern(/^[0-9]{6}$/)]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.userService.getProfile().subscribe({
      next: (data) => {
        this.profileForm.patchValue(data);
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load profile';
        this.isLoading = false;
      }
    });
  }

  selectEmoji(emoji: string): void {
    this.profileForm.patchValue({ profileEmoji: emoji });
    this.profileForm.markAsDirty();
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    const profileData: UserProfile = this.profileForm.getRawValue(); // Needed to get disabled email if we wanted sending it, but typically we send what API expects

    this.userService.updateProfile(profileData).subscribe({
      next: (updatedProfile) => {
        this.successMessage = 'Profile updated successfully!';
        this.isSaving = false;
        this.profileForm.markAsPristine();
      },
      error: (error) => {
        this.errorMessage = 'Failed to update profile';
        this.isSaving = false;
      }
    });
  }
}
