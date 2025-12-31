package com.ecomm.AuroraFlames.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleImageDTO {
    private Long id;
    private String imageName;
    private String imageUrl;
    private String contentType;
    private String imageData; // Base64 encoded image data
}
