/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;

public class FrodoKeyGenerationParameters
extends KeyGenerationParameters {
    private FrodoParameters params;

    public FrodoKeyGenerationParameters(SecureRandom secureRandom, FrodoParameters frodoParameters) {
        super(secureRandom, 256);
        this.params = frodoParameters;
    }

    public FrodoParameters getParameters() {
        return this.params;
    }
}

