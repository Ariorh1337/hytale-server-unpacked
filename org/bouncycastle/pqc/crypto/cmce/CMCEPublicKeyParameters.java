/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.pqc.crypto.cmce.CMCEKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.util.Arrays;

public class CMCEPublicKeyParameters
extends CMCEKeyParameters {
    private final byte[] publicKey;

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return this.getPublicKey();
    }

    public CMCEPublicKeyParameters(CMCEParameters cMCEParameters, byte[] byArray) {
        super(false, cMCEParameters);
        this.publicKey = Arrays.clone(byArray);
    }
}

