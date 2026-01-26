/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.saber.SABEREngine;
import org.bouncycastle.pqc.crypto.saber.SABERKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;

public class SABERKEMExtractor
implements EncapsulatedSecretExtractor {
    private SABEREngine engine;
    private SABERKeyParameters key;

    public SABERKEMExtractor(SABERKeyParameters sABERKeyParameters) {
        this.key = sABERKeyParameters;
        this.initCipher(this.key.getParameters());
    }

    private void initCipher(SABERParameters sABERParameters) {
        this.engine = sABERParameters.getEngine();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        byte[] byArray2 = new byte[this.engine.getSessionKeySize()];
        this.engine.crypto_kem_dec(byArray2, byArray, ((SABERPrivateKeyParameters)this.key).getPrivateKey());
        return byArray2;
    }

    @Override
    public int getEncapsulationLength() {
        return this.engine.getCipherTextSize();
    }
}

