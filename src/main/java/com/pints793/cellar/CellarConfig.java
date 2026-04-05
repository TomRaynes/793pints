package com.pints793.cellar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class CellarConfig {
    private long rackCooldownDefault;
    private long ventCooldownDefault;
    private long tapCooldownDefault;
    private long pullingPeriodDefault;
}
