package com.safewalk.model.enums;

import lombok.Getter;

@Getter
public enum RiskLevelEnum {
    BAIXO("Baixo"),
    MEDIO("Médio"),
    ALTO("Alto");

    private final String descricao;

    RiskLevelEnum(String descricao) {
        this.descricao = descricao;
    }

    public static RiskLevelEnum fromDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Tipo do risco não informado");
        }

        String normalizado = descricao.trim().toLowerCase();

        for (RiskLevelEnum value : RiskLevelEnum.values()) {
            if (value.getDescricao().toLowerCase().equals(normalizado)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Risco inválido: " + descricao);
    }
}
