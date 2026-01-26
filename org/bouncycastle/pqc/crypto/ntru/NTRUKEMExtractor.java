/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.pqc.crypto.ntru.NTRUOWCPA;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.OWCPADecryptResult;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

public class NTRUKEMExtractor
implements EncapsulatedSecretExtractor {
    private final NTRUPrivateKeyParameters ntruPrivateKey;

    public NTRUKEMExtractor(NTRUPrivateKeyParameters nTRUPrivateKeyParameters) {
        if (nTRUPrivateKeyParameters == null) {
            throw new NullPointerException("'ntruPrivateKey' cannot be null");
        }
        this.ntruPrivateKey = nTRUPrivateKeyParameters;
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        NTRUParameterSet nTRUParameterSet = this.ntruPrivateKey.getParameters().getParameterSet();
        if (byArray == null) {
            throw new NullPointerException("'encapsulation' cannot be null");
        }
        if (byArray.length != nTRUParameterSet.ntruCiphertextBytes()) {
            throw new IllegalArgumentException("encapsulation");
        }
        byte[] byArray2 = this.ntruPrivateKey.privateKey;
        NTRUOWCPA nTRUOWCPA = new NTRUOWCPA(nTRUParameterSet);
        OWCPADecryptResult oWCPADecryptResult = nTRUOWCPA.decrypt(byArray, byArray2);
        byte[] byArray3 = oWCPADecryptResult.rm;
        int n = oWCPADecryptResult.fail;
        SHA3Digest sHA3Digest = new SHA3Digest(256);
        byte[] byArray4 = new byte[sHA3Digest.getDigestSize()];
        sHA3Digest.update(byArray3, 0, byArray3.length);
        sHA3Digest.doFinal(byArray4, 0);
        sHA3Digest.update(byArray2, nTRUParameterSet.owcpaSecretKeyBytes(), nTRUParameterSet.prfKeyBytes());
        sHA3Digest.update(byArray, 0, byArray.length);
        sHA3Digest.doFinal(byArray3, 0);
        this.cmov(byArray4, byArray3, (byte)n);
        byte[] byArray5 = Arrays.copyOfRange(byArray4, 0, nTRUParameterSet.sharedKeyBytes());
        Arrays.clear(byArray4);
        return byArray5;
    }

    private void cmov(byte[] byArray, byte[] byArray2, byte by) {
        by = (byte)(~by + 1);
        for (int i = 0; i < byArray.length; ++i) {
            int n = i;
            byArray[n] = (byte)(byArray[n] ^ by & (byArray2[i] ^ byArray[i]));
        }
    }

    @Override
    public int getEncapsulationLength() {
        return this.ntruPrivateKey.getParameters().getParameterSet().ntruCiphertextBytes();
    }
}

