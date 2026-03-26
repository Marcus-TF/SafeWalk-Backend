package com.safewalk.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceRequest {
    
    @NotBlank(message = "Tipo é obrigatório")
    private String type;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Latitude é obrigatória")
    @Min(value = -90, message = "Latitude deve estar entre -90 e 90")
    @Max(value = 90, message = "Latitude deve estar entre -90 e 90")
    private Double latitude;
    
    @NotNull(message = "Longitude é obrigatória")
    @Min(value = -180, message = "Longitude deve estar entre -180 e 180")
    @Max(value = 180, message = "Longitude deve estar entre -180 e 180")
    private Double longitude;
    
    @NotBlank(message = "Localização é obrigatória")
    private String location;
    
    @NotBlank(message = "Nível de risco é obrigatório")
    @Pattern(regexp = "Baixo|Médio|Alto", message = "Risco deve ser: Baixo, Médio ou Alto")
    private String risk;

    @NotNull(message = "O campo 'anonymous' deve ser informado")
    private Boolean anonymous;
}
