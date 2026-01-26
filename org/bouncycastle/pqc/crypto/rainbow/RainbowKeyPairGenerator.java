/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyComputation;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.Version;

public class RainbowKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private RainbowKeyComputation rkc;
    private Version version;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        RainbowParameters rainbowParameters = ((RainbowKeyGenerationParameters)keyGenerationParameters).getParameters();
        this.rkc = new RainbowKeyComputation(rainbowParameters, keyGenerationParameters.getRandom());
        this.version = rainbowParameters.getVersion();
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        switch (this.version) {
            case CLASSIC: {
                return this.rkc.genKeyPairClassical();
            }
            case CIRCUMZENITHAL: {
                return this.rkc.genKeyPairCircumzenithal();
            }
            case COMPRESSED: {
                return this.rkc.genKeyPairCompressed();
            }
        }
        throw new IllegalArgumentException("No valid version. Please choose one of the following: classic, circumzenithal, compressed");
    }
}

