/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;

public class HQCKeyGenerationParameters
extends KeyGenerationParameters {
    private final HQCParameters params;

    public HQCKeyGenerationParameters(SecureRandom secureRandom, HQCParameters hQCParameters) {
        super(secureRandom, 256);
        this.params = hQCParameters;
    }

    public HQCParameters getParameters() {
        return this.params;
    }
}

