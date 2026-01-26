/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;

public class SABERKeyGenerationParameters
extends KeyGenerationParameters {
    private SABERParameters params;

    public SABERKeyGenerationParameters(SecureRandom secureRandom, SABERParameters sABERParameters) {
        super(secureRandom, 256);
        this.params = sABERParameters;
    }

    public SABERParameters getParameters() {
        return this.params;
    }
}

