package com.safewalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotspotDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String risk;
    private List<OccurrenceResponse> occurrencesList;
}
