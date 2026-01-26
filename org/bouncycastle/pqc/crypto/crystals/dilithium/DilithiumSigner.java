/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;

public class DilithiumSigner
implements MessageSigner {
    private DilithiumPrivateKeyParameters privKey;
    private DilithiumPublicKeyParameters pubKey;
    private SecureRandom random;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            if (cipherParameters instanceof ParametersWithRandom) {
                this.privKey = (DilithiumPrivateKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
                this.random = ((ParametersWithRandom)cipherParameters).getRandom();
            } else {
                this.privKey = (DilithiumPrivateKeyParameters)cipherParameters;
                this.random = null;
            }
        } else {
            this.pubKey = (DilithiumPublicKeyParameters)cipherParameters;
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        DilithiumEngine dilithiumEngine = this.privKey.getParameters().getEngine(this.random);
        return dilithiumEngine.sign(byArray, byArray.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2);
    }

    public byte[] internalGenerateSignature(byte[] byArray, byte[] byArray2) {
        DilithiumEngine dilithiumEngine = this.privKey.getParameters().getEngine(this.random);
        return dilithiumEngine.signSignatureInternal(byArray, byArray.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2, byArray2);
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        DilithiumEngine dilithiumEngine = this.pubKey.getParameters().getEngine(this.random);
        return dilithiumEngine.signOpen(byArray, byArray2, byArray2.length, this.pubKey.rho, this.pubKey.t1);
    }
}

