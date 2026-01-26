/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.hqc.HQCEngine;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.util.Arrays;

public class HQCKEMExtractor
implements EncapsulatedSecretExtractor {
    private HQCEngine engine;
    private final HQCKeyParameters key;

    public HQCKEMExtractor(HQCPrivateKeyParameters hQCPrivateKeyParameters) {
        this.key = hQCPrivateKeyParameters;
        this.initCipher(this.key.getParameters());
    }

    private void initCipher(HQCParameters hQCParameters) {
        this.engine = hQCParameters.getEngine();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        byte[] byArray2 = new byte[64];
        HQCPrivateKeyParameters hQCPrivateKeyParameters = (HQCPrivateKeyParameters)this.key;
        byte[] byArray3 = hQCPrivateKeyParameters.getPrivateKey();
        this.engine.decaps(byArray2, byArray, byArray3);
        return Arrays.copyOfRange(byArray2, 0, 32);
    }

    @Override
    public int getEncapsulationLength() {
        return this.key.getParameters().getN_BYTES() + this.key.getParameters().getN1N2_BYTES() + 16;
    }
}

