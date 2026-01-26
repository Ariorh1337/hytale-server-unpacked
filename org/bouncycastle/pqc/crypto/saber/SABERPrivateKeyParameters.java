/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.saber.SABERKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.util.Arrays;

public class SABERPrivateKeyParameters
extends SABERKeyParameters {
    private byte[] privateKey;

    public SABERPrivateKeyParameters(SABERParameters sABERParameters, byte[] byArray) {
        super(true, sABERParameters);
        this.privateKey = Arrays.clone(byArray);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }
}

