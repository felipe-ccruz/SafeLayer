package com.felp.backvm.dto;

import com.felp.backvm.domain.enums.UserPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePlanRequest {

    @NotNull
    private UserPlan plan;
}
