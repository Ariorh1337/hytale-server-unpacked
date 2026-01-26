/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.falcon.FalconNIST;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;

public class FalconSigner
implements MessageSigner {
    private byte[] encodedkey;
    private FalconNIST nist;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            if (cipherParameters instanceof ParametersWithRandom) {
                FalconPrivateKeyParameters falconPrivateKeyParameters = (FalconPrivateKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
                this.encodedkey = falconPrivateKeyParameters.getEncoded();
                this.nist = new FalconNIST(falconPrivateKeyParameters.getParameters().getLogN(), falconPrivateKeyParameters.getParameters().getNonceLength(), ((ParametersWithRandom)cipherParameters).getRandom());
            } else {
                FalconPrivateKeyParameters falconPrivateKeyParameters = (FalconPrivateKeyParameters)cipherParameters;
                this.encodedkey = ((FalconPrivateKeyParameters)cipherParameters).getEncoded();
                this.nist = new FalconNIST(falconPrivateKeyParameters.getParameters().getLogN(), falconPrivateKeyParameters.getParameters().getNonceLength(), CryptoServicesRegistrar.getSecureRandom());
            }
        } else {
            FalconPublicKeyParameters falconPublicKeyParameters = (FalconPublicKeyParameters)cipherParameters;
            this.encodedkey = falconPublicKeyParameters.getH();
            this.nist = new FalconNIST(falconPublicKeyParameters.getParameters().getLogN(), falconPublicKeyParameters.getParameters().getNonceLength(), CryptoServicesRegistrar.getSecureRandom());
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        byte[] byArray2 = new byte[this.nist.CRYPTO_BYTES];
        return this.nist.crypto_sign(byArray2, byArray, byArray.length, this.encodedkey);
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        if (byArray2[0] != (byte)(48 + this.nist.LOGN)) {
            return false;
        }
        byte[] byArray3 = new byte[this.nist.NONCELEN];
        byte[] byArray4 = new byte[byArray2.length - this.nist.NONCELEN - 1];
        System.arraycopy(byArray2, 1, byArray3, 0, this.nist.NONCELEN);
        System.arraycopy(byArray2, this.nist.NONCELEN + 1, byArray4, 0, byArray2.length - this.nist.NONCELEN - 1);
        return this.nist.crypto_sign_open(byArray4, byArray3, byArray, this.encodedkey) == 0;
    }
}

