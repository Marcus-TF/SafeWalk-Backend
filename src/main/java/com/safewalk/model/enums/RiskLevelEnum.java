package com.safewalk.model.enums;

import lombok.Getter;

@Getter
public enum RiskLevelEnum {
    LOW("Baixo"),
    MEDIUM("Médio"),
    HIGH("Alto");

    private final String description;

    RiskLevelEnum(String description) {
        this.description = description;
    }

    public static RiskLevelEnum fromDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Tipo do risco não informado");
        }

        String normalized = description.trim().toLowerCase();

        for (RiskLevelEnum value : RiskLevelEnum.values()) {
            if (value.getDescription().toLowerCase().equals(normalized)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Risco inválido: " + description);
    }
}
