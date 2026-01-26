/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;

public class SnovaKeyGenerationParameters
extends KeyGenerationParameters {
    private final SnovaParameters params;

    public SnovaKeyGenerationParameters(SecureRandom secureRandom, SnovaParameters snovaParameters) {
        super(secureRandom, -1);
        this.params = snovaParameters;
    }

    public SnovaParameters getParameters() {
        return this.params;
    }
}

