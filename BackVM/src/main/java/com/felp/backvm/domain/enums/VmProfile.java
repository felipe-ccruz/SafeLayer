package com.felp.backvm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VmProfile {

    WEAK(2, 4, 60, "Fraco"),
    MEDIUM(4, 8, 100, "Médio"),
    STRONG(8, 16, 200, "Forte");

    private final int cpuCores;
    private final int ramGb;
    private final int diskGb;
    private final String label;
}
