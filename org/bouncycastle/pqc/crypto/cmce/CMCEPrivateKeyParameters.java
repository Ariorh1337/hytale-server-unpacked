/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.pqc.crypto.cmce.CMCEEngine;
import org.bouncycastle.pqc.crypto.cmce.CMCEKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.util.Arrays;

public class CMCEPrivateKeyParameters
extends CMCEKeyParameters {
    private final byte[] privateKey;

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public CMCEPrivateKeyParameters(CMCEParameters cMCEParameters, byte[] byArray) {
        super(true, cMCEParameters);
        this.privateKey = Arrays.clone(byArray);
    }

    public CMCEPrivateKeyParameters(CMCEParameters cMCEParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        super(true, cMCEParameters);
        int n = byArray.length + byArray2.length + byArray3.length + byArray4.length + byArray5.length;
        this.privateKey = new byte[n];
        int n2 = 0;
        System.arraycopy(byArray, 0, this.privateKey, n2, byArray.length);
        System.arraycopy(byArray2, 0, this.privateKey, n2 += byArray.length, byArray2.length);
        System.arraycopy(byArray3, 0, this.privateKey, n2 += byArray2.length, byArray3.length);
        System.arraycopy(byArray4, 0, this.privateKey, n2 += byArray3.length, byArray4.length);
        System.arraycopy(byArray5, 0, this.privateKey, n2 += byArray4.length, byArray5.length);
    }

    public byte[] reconstructPublicKey() {
        CMCEEngine cMCEEngine = this.getParameters().getEngine();
        return cMCEEngine.generate_public_key_from_private_key(this.privateKey);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getDelta() {
        return Arrays.copyOfRange(this.privateKey, 0, 32);
    }

    public byte[] getC() {
        return Arrays.copyOfRange(this.privateKey, 32, 40);
    }

    public byte[] getG() {
        return Arrays.copyOfRange(this.privateKey, 40, 40 + this.getParameters().getT() * 2);
    }

    public byte[] getAlpha() {
        return Arrays.copyOfRange(this.privateKey, 40 + this.getParameters().getT() * 2, this.privateKey.length - 32);
    }

    public byte[] getS() {
        return Arrays.copyOfRange(this.privateKey, this.privateKey.length - 32, this.privateKey.length);
    }
}

