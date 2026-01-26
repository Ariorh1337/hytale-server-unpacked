/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.frodo.FrodoEngine;
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;

public class FrodoKEMExtractor
implements EncapsulatedSecretExtractor {
    private FrodoEngine engine;
    private FrodoKeyParameters key;

    public FrodoKEMExtractor(FrodoKeyParameters frodoKeyParameters) {
        this.key = frodoKeyParameters;
        this.initCipher(this.key.getParameters());
    }

    private void initCipher(FrodoParameters frodoParameters) {
        this.engine = frodoParameters.getEngine();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        byte[] byArray2 = new byte[this.engine.getSessionKeySize()];
        this.engine.kem_dec(byArray2, byArray, ((FrodoPrivateKeyParameters)this.key).getPrivateKey());
        return byArray2;
    }

    @Override
    public int getEncapsulationLength() {
        return this.engine.getCipherTextSize();
    }
}

