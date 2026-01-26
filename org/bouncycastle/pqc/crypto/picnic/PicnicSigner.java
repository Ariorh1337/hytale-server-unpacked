/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class PicnicSigner
implements MessageSigner {
    private PicnicPrivateKeyParameters privKey;
    private PicnicPublicKeyParameters pubKey;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            this.privKey = (PicnicPrivateKeyParameters)cipherParameters;
        } else {
            this.pubKey = (PicnicPublicKeyParameters)cipherParameters;
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        PicnicEngine picnicEngine = this.privKey.getParameters().getEngine();
        byte[] byArray2 = new byte[picnicEngine.getSignatureSize(byArray.length)];
        picnicEngine.crypto_sign(byArray2, byArray, this.privKey.getEncoded());
        byte[] byArray3 = new byte[picnicEngine.getTrueSignatureSize()];
        System.arraycopy(byArray2, byArray.length + 4, byArray3, 0, picnicEngine.getTrueSignatureSize());
        return byArray3;
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        PicnicEngine picnicEngine = this.pubKey.getParameters().getEngine();
        byte[] byArray3 = new byte[byArray.length];
        byte[] byArray4 = Arrays.concatenate(Pack.intToLittleEndian(byArray2.length), byArray, byArray2);
        boolean bl = picnicEngine.crypto_sign_open(byArray3, byArray4, this.pubKey.getEncoded());
        if (!Arrays.areEqual(byArray, byArray3)) {
            return false;
        }
        return bl;
    }
}

