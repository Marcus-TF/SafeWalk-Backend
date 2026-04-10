package com.safewalk.model.enums;

import lombok.Getter;

@Getter
public enum OccurrenceEnum {
    ROBBERY("Assalto"),
    THEFT("Furto"),
    VEHICLE_THEFT("Roubo de Veículo"),
    VANDALISM("Vandalismo"),
    SUSPICIOUS_PERSON("Pessoa Suspeita"),
    POOR_LIGHTING("Iluminação Precária"),
    DESERTED_AREA("Área Deserta"),
    OTHER("Outro");

    private final String description;

    OccurrenceEnum(String description) {
        this.description = description;
    }

    public static OccurrenceEnum fromDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Tipo da ocorrência não informado");
        }

        String normalized = description.trim().toLowerCase();

        for (OccurrenceEnum value : OccurrenceEnum.values()) {
            if (value.getDescription().toLowerCase().equals(normalized)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Tipo inválido: " + description);
    }
}
