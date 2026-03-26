package com.safewalk.model.enums;

import lombok.Getter;

@Getter
public enum OccurrenceEnum {
    ASSALTO("Assalto"),
    FURTO("Furto"),
    ROUBO_DE_VEICULO("Roubo de Veículo"),
    VANDALISMO("Vandalismo"),
    PESSOA_SUSPEITA("Pessoa Suspeita"),
    ILUMINACAO_PRECARIA("Iluminação Precária"),
    AREA_DESERTA("Área Deserta"),
    OUTRO("Outro");

    private final String descricao;

    OccurrenceEnum(String descricao) {
        this.descricao = descricao;
    }

    public static OccurrenceEnum fromDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Tipo da ocorrência não informado");
        }

        String normalizado = descricao.trim().toLowerCase();

        for (OccurrenceEnum value : OccurrenceEnum.values()) {
            if (value.getDescricao().toLowerCase().equals(normalizado)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Tipo inválido: " + descricao);
    }
}
