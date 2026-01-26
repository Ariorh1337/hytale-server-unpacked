/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.mayo.GF16Utils;
import org.bouncycastle.pqc.crypto.mayo.MayoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Longs;

public class MayoKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private MayoParameters p;
    private SecureRandom random;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.p = ((MayoKeyGenerationParameters)keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        int n = this.p.getMVecLimbs();
        int n2 = this.p.getM();
        int n3 = this.p.getV();
        int n4 = this.p.getO();
        int n5 = this.p.getOBytes();
        int n6 = this.p.getP1Limbs();
        int n7 = this.p.getP3Limbs();
        int n8 = this.p.getPkSeedBytes();
        int n9 = this.p.getSkSeedBytes();
        byte[] byArray = new byte[this.p.getCpkBytes()];
        byte[] byArray2 = new byte[this.p.getCskBytes()];
        byte[] byArray3 = new byte[n8 + n5];
        long[] lArray = new long[n6 + this.p.getP2Limbs()];
        long[] lArray2 = new long[n4 * n4 * n];
        byte[] byArray4 = new byte[n3 * n4];
        this.random.nextBytes(byArray2);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray2, 0, n9);
        sHAKEDigest.doFinal(byArray3, 0, n8 + n5);
        GF16.decode(byArray3, n8, byArray4, 0, byArray4.length);
        Utils.expandP1P2(this.p, lArray, byArray3);
        GF16Utils.mulAddMUpperTriangularMatXMat(n, lArray, byArray4, lArray, n6, n3, n4);
        GF16Utils.mulAddMatTransXMMat(n, byArray4, lArray, n6, lArray2, n3, n4);
        System.arraycopy(byArray3, 0, byArray, 0, n8);
        long[] lArray3 = new long[n7];
        int n10 = 0;
        int n11 = n4 * n;
        int n12 = 0;
        int n13 = 0;
        int n14 = 0;
        while (n12 < n4) {
            int n15 = n12;
            int n16 = n13;
            int n17 = n14;
            while (n15 < n4) {
                System.arraycopy(lArray2, n14 + n16, lArray3, n10, n);
                if (n12 != n15) {
                    Longs.xorTo(n, lArray2, n17 + n13, lArray3, n10);
                }
                n10 += n;
                ++n15;
                n16 += n;
                n17 += n11;
            }
            ++n12;
            n14 += n11;
            n13 += n;
        }
        Utils.packMVecs(lArray3, byArray, n8, n7 / n, n2);
        Arrays.clear(byArray4);
        Arrays.clear(lArray2);
        return new AsymmetricCipherKeyPair(new MayoPublicKeyParameters(this.p, byArray), new MayoPrivateKeyParameters(this.p, byArray2));
    }
}

