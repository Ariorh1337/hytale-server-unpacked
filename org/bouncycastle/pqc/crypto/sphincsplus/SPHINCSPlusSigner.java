/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.sphincsplus.ADRS;
import org.bouncycastle.pqc.crypto.sphincsplus.Fors;
import org.bouncycastle.pqc.crypto.sphincsplus.HT;
import org.bouncycastle.pqc.crypto.sphincsplus.IndexedDigest;
import org.bouncycastle.pqc.crypto.sphincsplus.SIG;
import org.bouncycastle.pqc.crypto.sphincsplus.SIG_FORS;
import org.bouncycastle.pqc.crypto.sphincsplus.SIG_XMSS;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusEngine;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class SPHINCSPlusSigner
implements MessageSigner {
    private SPHINCSPlusPrivateKeyParameters privKey;
    private SPHINCSPlusPublicKeyParameters pubKey;
    private SecureRandom random;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            if (cipherParameters instanceof ParametersWithRandom) {
                this.privKey = (SPHINCSPlusPrivateKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
                this.random = ((ParametersWithRandom)cipherParameters).getRandom();
            } else {
                this.privKey = (SPHINCSPlusPrivateKeyParameters)cipherParameters;
            }
        } else {
            this.pubKey = (SPHINCSPlusPublicKeyParameters)cipherParameters;
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        SPHINCSPlusEngine sPHINCSPlusEngine = this.privKey.getParameters().getEngine();
        sPHINCSPlusEngine.init(this.privKey.pk.seed);
        byte[] byArray2 = new byte[sPHINCSPlusEngine.N];
        if (this.random != null) {
            this.random.nextBytes(byArray2);
        } else {
            System.arraycopy(this.privKey.pk.seed, 0, byArray2, 0, byArray2.length);
        }
        Fors fors = new Fors(sPHINCSPlusEngine);
        byte[] byArray3 = sPHINCSPlusEngine.PRF_msg(this.privKey.sk.prf, byArray2, byArray);
        IndexedDigest indexedDigest = sPHINCSPlusEngine.H_msg(byArray3, this.privKey.pk.seed, this.privKey.pk.root, byArray);
        byte[] byArray4 = indexedDigest.digest;
        long l = indexedDigest.idx_tree;
        int n = indexedDigest.idx_leaf;
        ADRS aDRS = new ADRS();
        aDRS.setTypeAndClear(3);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        SIG_FORS[] sIG_FORSArray = fors.sign(byArray4, this.privKey.sk.seed, this.privKey.pk.seed, aDRS);
        aDRS = new ADRS();
        aDRS.setTypeAndClear(3);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        byte[] byArray5 = fors.pkFromSig(sIG_FORSArray, byArray4, this.privKey.pk.seed, aDRS);
        ADRS aDRS2 = new ADRS();
        aDRS2.setTypeAndClear(2);
        HT hT = new HT(sPHINCSPlusEngine, this.privKey.getSeed(), this.privKey.getPublicSeed());
        byte[] byArray6 = hT.sign(byArray5, l, n);
        byte[][] byArrayArray = new byte[sIG_FORSArray.length + 2][];
        byArrayArray[0] = byArray3;
        for (int i = 0; i != sIG_FORSArray.length; ++i) {
            byArrayArray[1 + i] = Arrays.concatenate(sIG_FORSArray[i].sk, Arrays.concatenate(sIG_FORSArray[i].authPath));
        }
        byArrayArray[byArrayArray.length - 1] = byArray6;
        return Arrays.concatenate(byArrayArray);
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        SPHINCSPlusEngine sPHINCSPlusEngine = this.pubKey.getParameters().getEngine();
        sPHINCSPlusEngine.init(this.pubKey.getSeed());
        ADRS aDRS = new ADRS();
        SIG sIG = new SIG(sPHINCSPlusEngine.N, sPHINCSPlusEngine.K, sPHINCSPlusEngine.A, sPHINCSPlusEngine.D, sPHINCSPlusEngine.H_PRIME, sPHINCSPlusEngine.WOTS_LEN, byArray2);
        byte[] byArray3 = sIG.getR();
        SIG_FORS[] sIG_FORSArray = sIG.getSIG_FORS();
        SIG_XMSS[] sIG_XMSSArray = sIG.getSIG_HT();
        IndexedDigest indexedDigest = sPHINCSPlusEngine.H_msg(byArray3, this.pubKey.getSeed(), this.pubKey.getRoot(), byArray);
        byte[] byArray4 = indexedDigest.digest;
        long l = indexedDigest.idx_tree;
        int n = indexedDigest.idx_leaf;
        aDRS.setTypeAndClear(3);
        aDRS.setLayerAddress(0);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        byte[] byArray5 = new Fors(sPHINCSPlusEngine).pkFromSig(sIG_FORSArray, byArray4, this.pubKey.getSeed(), aDRS);
        aDRS.setTypeAndClear(2);
        aDRS.setLayerAddress(0);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        HT hT = new HT(sPHINCSPlusEngine, null, this.pubKey.getSeed());
        return hT.verify(byArray5, sIG_XMSSArray, this.pubKey.getSeed(), l, n, this.pubKey.getRoot());
    }
}

