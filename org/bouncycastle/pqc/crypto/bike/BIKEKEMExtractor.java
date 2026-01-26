/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.bike.BIKEEngine;
import org.bouncycastle.pqc.crypto.bike.BIKEKeyParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.util.Arrays;

public class BIKEKEMExtractor
implements EncapsulatedSecretExtractor {
    private BIKEEngine engine;
    private BIKEKeyParameters key;

    public BIKEKEMExtractor(BIKEPrivateKeyParameters bIKEPrivateKeyParameters) {
        this.key = bIKEPrivateKeyParameters;
        this.initCipher(this.key.getParameters());
    }

    private void initCipher(BIKEParameters bIKEParameters) {
        this.engine = bIKEParameters.getEngine();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        byte[] byArray2 = new byte[this.engine.getSessionKeySize()];
        BIKEPrivateKeyParameters bIKEPrivateKeyParameters = (BIKEPrivateKeyParameters)this.key;
        byte[] byArray3 = Arrays.copyOfRange(byArray, 0, bIKEPrivateKeyParameters.getParameters().getRByte());
        byte[] byArray4 = Arrays.copyOfRange(byArray, bIKEPrivateKeyParameters.getParameters().getRByte(), byArray.length);
        byte[] byArray5 = bIKEPrivateKeyParameters.getH0();
        byte[] byArray6 = bIKEPrivateKeyParameters.getH1();
        byte[] byArray7 = bIKEPrivateKeyParameters.getSigma();
        this.engine.decaps(byArray2, byArray5, byArray6, byArray7, byArray3, byArray4);
        return Arrays.copyOfRange(byArray2, 0, this.key.getParameters().getSessionKeySize() / 8);
    }

    @Override
    public int getEncapsulationLength() {
        return this.key.getParameters().getRByte() + this.key.getParameters().getLByte();
    }
}

