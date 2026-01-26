/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMS;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSignature;

public class LMSSigner
implements MessageSigner {
    private LMSPrivateKeyParameters privKey;
    private LMSPublicKeyParameters pubKey;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            if (cipherParameters instanceof HSSPrivateKeyParameters) {
                HSSPrivateKeyParameters hSSPrivateKeyParameters = (HSSPrivateKeyParameters)cipherParameters;
                if (hSSPrivateKeyParameters.getL() != 1) throw new IllegalArgumentException("only a single level HSS key can be used with LMS");
                this.privKey = hSSPrivateKeyParameters.getRootKey();
                return;
            } else {
                this.privKey = (LMSPrivateKeyParameters)cipherParameters;
            }
            return;
        } else if (cipherParameters instanceof HSSPublicKeyParameters) {
            HSSPublicKeyParameters hSSPublicKeyParameters = (HSSPublicKeyParameters)cipherParameters;
            if (hSSPublicKeyParameters.getL() != 1) throw new IllegalArgumentException("only a single level HSS key can be used with LMS");
            this.pubKey = hSSPublicKeyParameters.getLMSPublicKey();
            return;
        } else {
            this.pubKey = (LMSPublicKeyParameters)cipherParameters;
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        try {
            return LMS.generateSign(this.privKey, byArray).getEncoded();
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to encode signature: " + iOException.getMessage());
        }
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        try {
            return LMS.verifySignature(this.pubKey, LMSSignature.getInstance(byArray2), byArray);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to decode signature: " + iOException.getMessage());
        }
    }
}

