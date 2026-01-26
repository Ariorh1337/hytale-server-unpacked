/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECCSIKeyGenerationParameters
extends KeyGenerationParameters {
    private final BigInteger q;
    private final ECPoint G;
    private final Digest digest;
    private final byte[] id;
    private final BigInteger ksak;
    private final ECPoint kpak;
    private final int n;

    public ECCSIKeyGenerationParameters(SecureRandom secureRandom, X9ECParameters x9ECParameters, Digest digest, byte[] byArray) {
        super(secureRandom, x9ECParameters.getCurve().getA().bitLength());
        this.q = x9ECParameters.getCurve().getOrder();
        this.G = x9ECParameters.getG();
        this.digest = digest;
        this.id = Arrays.clone(byArray);
        this.n = x9ECParameters.getCurve().getA().bitLength();
        this.ksak = BigIntegers.createRandomBigInteger(this.n, secureRandom).mod(this.q);
        this.kpak = this.G.multiply(this.ksak).normalize();
    }

    public byte[] getId() {
        return Arrays.clone(this.id);
    }

    public ECPoint getKPAK() {
        return this.kpak;
    }

    public BigInteger computeSSK(BigInteger bigInteger) {
        return this.ksak.add(bigInteger).mod(this.q);
    }

    public BigInteger getQ() {
        return this.q;
    }

    public ECPoint getG() {
        return this.G;
    }

    public Digest getDigest() {
        return this.digest;
    }

    public int getN() {
        return this.n;
    }
}

