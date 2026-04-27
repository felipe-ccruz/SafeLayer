package com.felp.backvm.dto;

import com.felp.backvm.domain.enums.OsType;
import com.felp.backvm.domain.enums.VmProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVmRequest {

    @NotBlank
    private String name;

    @NotNull
    private OsType osType;

    @NotNull
    private VmProfile profile;
}
