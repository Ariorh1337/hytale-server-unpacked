/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;

public class PicnicKeyGenerationParameters
extends KeyGenerationParameters {
    private final PicnicParameters parameters;

    public PicnicKeyGenerationParameters(SecureRandom secureRandom, PicnicParameters picnicParameters) {
        super(secureRandom, -1);
        this.parameters = picnicParameters;
    }

    public PicnicParameters getParameters() {
        return this.parameters;
    }
}

