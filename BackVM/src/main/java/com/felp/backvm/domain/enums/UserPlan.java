package com.felp.backvm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum UserPlan {

    BASIC("Basic", 0, 1, Set.of(VmProfile.WEAK)),
    STANDARD("Standard", 1990, 3, Set.of(VmProfile.WEAK, VmProfile.MEDIUM)),
    PREMIUM("Premium", 4990, Integer.MAX_VALUE, Set.of(VmProfile.WEAK, VmProfile.MEDIUM, VmProfile.STRONG));

    private final String label;
    private final int priceCents;
    private final int maxVms;
    private final Set<VmProfile> allowedProfiles;

    public boolean allowsProfile(VmProfile profile) {
        return allowedProfiles.contains(profile);
    }

    public boolean isUnlimited() {
        return maxVms == Integer.MAX_VALUE;
    }
}
