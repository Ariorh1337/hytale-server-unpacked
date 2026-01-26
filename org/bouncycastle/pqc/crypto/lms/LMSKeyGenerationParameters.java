/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LmsUtils;

public class LMSKeyGenerationParameters
extends KeyGenerationParameters {
    private final LMSParameters lmsParameters;

    public LMSKeyGenerationParameters(LMSParameters lMSParameters, SecureRandom secureRandom) {
        super(secureRandom, LmsUtils.calculateStrength(lMSParameters));
        this.lmsParameters = lMSParameters;
    }

    public LMSParameters getParameters() {
        return this.lmsParameters;
    }
}

