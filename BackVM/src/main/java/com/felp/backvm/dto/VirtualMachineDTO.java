package com.felp.backvm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachineDTO {

    private Long id;
    private String name;
    private String osType;
    private String osLabel;
    private String profile;
    private String profileLabel;
    private String status;
    private String statusLabel;
    private Integer cpuCores;
    private Integer ramGb;
    private Integer diskGb;
    private String createdAt;
}
