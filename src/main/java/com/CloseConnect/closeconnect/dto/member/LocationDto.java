package com.CloseConnect.closeconnect.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDto {
    private Double latitude;
    private Double longitude;
    private Double radius;
}
