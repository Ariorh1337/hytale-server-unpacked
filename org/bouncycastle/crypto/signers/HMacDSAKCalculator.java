/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class HMacDSAKCalculator
implements DSAKCalculator {
    private final HMac hMac;
    private final byte[] K;
    private final byte[] V;
    private BigInteger n;

    public HMacDSAKCalculator(Digest digest) {
        this.hMac = new HMac(digest);
        int n = this.hMac.getMacSize();
        this.V = new byte[n];
        this.K = new byte[n];
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

    @Override
    public void init(BigInteger bigInteger, SecureRandom secureRandom) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override
    public void init(BigInteger bigInteger, BigInteger bigInteger2, byte[] byArray) {
        this.n = bigInteger;
        BigInteger bigInteger3 = this.bitsToInt(byArray);
        if (bigInteger3.compareTo(bigInteger) >= 0) {
            bigInteger3 = bigInteger3.subtract(bigInteger);
        }
        int n = BigIntegers.getUnsignedByteLength(bigInteger);
        byte[] byArray2 = BigIntegers.asUnsignedByteArray(n, bigInteger2);
        byte[] byArray3 = BigIntegers.asUnsignedByteArray(n, bigInteger3);
        Arrays.fill(this.K, (byte)0);
        Arrays.fill(this.V, (byte)1);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)0);
        this.hMac.update(byArray2, 0, byArray2.length);
        this.hMac.update(byArray3, 0, byArray3.length);
        this.initAdditionalInput0(this.hMac);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)1);
        this.hMac.update(byArray2, 0, byArray2.length);
        this.hMac.update(byArray3, 0, byArray3.length);
        this.initAdditionalInput1(this.hMac);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
    }

    @Override
    public BigInteger nextK() {
        byte[] byArray = new byte[BigIntegers.getUnsignedByteLength(this.n)];
        while (true) {
            int n;
            for (int i = 0; i < byArray.length; i += n) {
                this.hMac.update(this.V, 0, this.V.length);
                this.hMac.doFinal(this.V, 0);
                n = Math.min(byArray.length - i, this.V.length);
                System.arraycopy(this.V, 0, byArray, i, n);
            }
            BigInteger bigInteger = this.bitsToInt(byArray);
            if (bigInteger.signum() > 0 && bigInteger.compareTo(this.n) < 0) {
                return bigInteger;
            }
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.update((byte)0);
            this.hMac.doFinal(this.K, 0);
            this.hMac.init(new KeyParameter(this.K));
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
        }
    }

    protected void initAdditionalInput0(HMac hMac) {
    }

    protected void initAdditionalInput1(HMac hMac) {
    }

    private BigInteger bitsToInt(byte[] byArray) {
        int n = byArray.length * 8;
        int n2 = this.n.bitLength();
        BigInteger bigInteger = BigIntegers.fromUnsignedByteArray(byArray);
        if (n > n2) {
            bigInteger = bigInteger.shiftRight(n - n2);
        }
        return bigInteger;
    }
}

