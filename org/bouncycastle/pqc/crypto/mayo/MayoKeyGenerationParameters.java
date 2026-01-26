/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;

public class MayoKeyGenerationParameters
extends KeyGenerationParameters {
    private final MayoParameters params;

    public MayoKeyGenerationParameters(SecureRandom secureRandom, MayoParameters mayoParameters) {
        super(secureRandom, 256);
        this.params = mayoParameters;
    }

    public MayoParameters getParameters() {
        return this.params;
    }
}

