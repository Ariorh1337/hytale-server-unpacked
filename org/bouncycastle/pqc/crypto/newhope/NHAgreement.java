/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NewHope;

public class NHAgreement {
    private NHPrivateKeyParameters privKey;

    public void init(CipherParameters cipherParameters) {
        this.privKey = (NHPrivateKeyParameters)cipherParameters;
    }

    public byte[] calculateAgreement(CipherParameters cipherParameters) {
        NHPublicKeyParameters nHPublicKeyParameters = (NHPublicKeyParameters)cipherParameters;
        byte[] byArray = new byte[32];
        NewHope.sharedA(byArray, this.privKey.secData, nHPublicKeyParameters.pubData);
        return byArray;
    }
}

