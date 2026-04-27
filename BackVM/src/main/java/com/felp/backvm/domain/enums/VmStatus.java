package com.felp.backvm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VmStatus {

    RUNNING("Rodando"),
    STOPPED("Parado"),
    PAUSED("Pausado");

    private final String label;
}
