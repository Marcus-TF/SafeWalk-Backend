package com.safewalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceResponse {
    private Long id;
    private String type;
    private String description;
    private Double latitude;
    private Double longitude;
    private String location;
    private String risk;
    private Long userId;
    private String userName;
    private String createdAt;
    private boolean isActive;
}
