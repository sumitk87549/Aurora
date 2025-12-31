package com.ecomm.AuroraFlames.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String profileEmoji;
    private String defaultAddress;
    private String city;
    private String state;
    private String pincode;
}
