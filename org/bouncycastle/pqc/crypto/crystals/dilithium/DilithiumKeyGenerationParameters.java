/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;

public class DilithiumKeyGenerationParameters
extends KeyGenerationParameters {
    private final DilithiumParameters params;

    public DilithiumKeyGenerationParameters(SecureRandom secureRandom, DilithiumParameters dilithiumParameters) {
        super(secureRandom, 256);
        this.params = dilithiumParameters;
    }

    public DilithiumParameters getParameters() {
        return this.params;
    }
}

