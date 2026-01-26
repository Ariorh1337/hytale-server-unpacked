/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;

public class SLHDSAKeyGenerationParameters
extends KeyGenerationParameters {
    private final SLHDSAParameters parameters;

    public SLHDSAKeyGenerationParameters(SecureRandom secureRandom, SLHDSAParameters sLHDSAParameters) {
        super(secureRandom, -1);
        this.parameters = sLHDSAParameters;
    }

    SLHDSAParameters getParameters() {
        return this.parameters;
    }
}

