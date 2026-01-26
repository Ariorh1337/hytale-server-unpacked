/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;

public class MLKEMExtractor
implements EncapsulatedSecretExtractor {
    private final MLKEMPrivateKeyParameters privateKey;
    private final MLKEMEngine engine;

    public MLKEMExtractor(MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters) {
        if (mLKEMPrivateKeyParameters == null) {
            throw new NullPointerException("'privateKey' cannot be null");
        }
        this.privateKey = mLKEMPrivateKeyParameters;
        this.engine = mLKEMPrivateKeyParameters.getParameters().getEngine();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        return this.engine.kemDecrypt(this.privateKey, byArray);
    }

    @Override
    public int getEncapsulationLength() {
        return this.engine.getCryptoCipherTextBytes();
    }
}

