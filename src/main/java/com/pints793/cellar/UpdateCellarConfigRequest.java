package com.pints793.cellar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class UpdateCellarConfigRequest {
    private Field rackCooldownDefault;
    private Field ventCooldownDefault;
    private Field tapCooldownDefault;
    private Field pullingPeriodDefault;

    @Getter @Setter @Accessors(chain = true)
    public static class Field {
        private long value;
        private boolean applyToAll;
    }
}
