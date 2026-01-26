/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ECCSIPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECCSISigner
implements Signer {
    private final BigInteger q;
    private final ECPoint G;
    private final Digest digest;
    private BigInteger j;
    private BigInteger r;
    private ECPoint Y;
    private final ECPoint kpak;
    private final byte[] id;
    private CipherParameters param;
    private ByteArrayOutputStream stream;
    private boolean forSigning;
    private final int N;

    public ECCSISigner(ECPoint eCPoint, X9ECParameters x9ECParameters, Digest digest, byte[] byArray) {
        this.kpak = eCPoint;
        this.id = byArray;
        this.q = x9ECParameters.getCurve().getOrder();
        this.G = x9ECParameters.getG();
        this.digest = digest;
        this.digest.reset();
        this.N = x9ECParameters.getCurve().getOrder().bitLength() + 7 >> 3;
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forSigning = bl;
        this.param = cipherParameters;
        this.reset();
    }

    @Override
    public void update(byte by) {
        if (this.forSigning) {
            this.digest.update(by);
        } else {
            this.stream.write(by);
        }
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        if (this.forSigning) {
            this.digest.update(byArray, n, n2);
        } else {
            this.stream.write(byArray, n, n2);
        }
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        byte[] byArray = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray, 0);
        ECCSIPrivateKeyParameters eCCSIPrivateKeyParameters = (ECCSIPrivateKeyParameters)((ParametersWithRandom)this.param).getParameters();
        BigInteger bigInteger = eCCSIPrivateKeyParameters.getSSK();
        BigInteger bigInteger2 = new BigInteger(1, byArray).add(this.r.multiply(bigInteger)).mod(this.q);
        if (bigInteger2.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Invalid j, retry");
        }
        BigInteger bigInteger3 = bigInteger2.modInverse(this.q).multiply(this.j).mod(this.q);
        return Arrays.concatenate(BigIntegers.asUnsignedByteArray(this.N, this.r), BigIntegers.asUnsignedByteArray(this.N, bigInteger3), eCCSIPrivateKeyParameters.getPublicKeyParameters().getPVT().getEncoded(false));
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        byte[] byArray2 = Arrays.copyOf(byArray, this.N);
        BigInteger bigInteger = new BigInteger(1, Arrays.copyOfRange(byArray, this.N, this.N << 1));
        this.r = new BigInteger(1, byArray2).mod(this.q);
        this.digest.update(byArray2, 0, this.N);
        byArray2 = this.stream.toByteArray();
        this.digest.update(byArray2, 0, byArray2.length);
        byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray2, 0);
        BigInteger bigInteger2 = new BigInteger(1, byArray2).mod(this.q);
        ECPoint eCPoint = this.G.multiply(bigInteger2).normalize();
        ECPoint eCPoint2 = this.Y.multiply(this.r).normalize();
        ECPoint eCPoint3 = eCPoint.add(eCPoint2).normalize();
        ECPoint eCPoint4 = eCPoint3.multiply(bigInteger).normalize();
        BigInteger bigInteger3 = eCPoint4.getAffineXCoord().toBigInteger();
        return bigInteger3.mod(this.q).equals(this.r.mod(this.q));
    }

    @Override
    public void reset() {
        Object object;
        ECPoint eCPoint;
        Object object2;
        this.digest.reset();
        CipherParameters cipherParameters = this.param;
        SecureRandom secureRandom = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            secureRandom = ((ParametersWithRandom)cipherParameters).getRandom();
            cipherParameters = ((ParametersWithRandom)cipherParameters).getParameters();
        }
        ECPoint eCPoint2 = null;
        if (this.forSigning) {
            object2 = (ECCSIPrivateKeyParameters)cipherParameters;
            eCPoint = ((ECCSIPrivateKeyParameters)object2).getPublicKeyParameters().getPVT();
            this.j = BigIntegers.createRandomBigInteger(this.q.bitLength(), secureRandom);
            object = this.G.multiply(this.j).normalize();
            this.r = ((ECPoint)object).getAffineXCoord().toBigInteger().mod(this.q);
            eCPoint2 = this.G.multiply(((ECCSIPrivateKeyParameters)object2).getSSK());
        } else {
            object2 = (ECCSIPublicKeyParameters)cipherParameters;
            eCPoint = ((ECCSIPublicKeyParameters)object2).getPVT();
            this.stream = new ByteArrayOutputStream();
        }
        object2 = this.G.getEncoded(false);
        this.digest.update((byte[])object2, 0, ((Object)object2).length);
        object2 = this.kpak.getEncoded(false);
        this.digest.update((byte[])object2, 0, ((Object)object2).length);
        this.digest.update(this.id, 0, this.id.length);
        object2 = eCPoint.getEncoded(false);
        this.digest.update((byte[])object2, 0, ((Object)object2).length);
        object2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal((byte[])object2, 0);
        object = new BigInteger(1, (byte[])object2).mod(this.q);
        this.digest.update((byte[])object2, 0, ((Object)object2).length);
        if (this.forSigning) {
            if (!(eCPoint2 = eCPoint2.subtract(eCPoint.multiply((BigInteger)object)).normalize()).equals(this.kpak)) {
                throw new IllegalArgumentException("Invalid KPAK");
            }
            byte[] byArray = BigIntegers.asUnsignedByteArray(this.N, this.r);
            this.digest.update(byArray, 0, byArray.length);
        } else {
            this.Y = eCPoint.multiply((BigInteger)object).add(this.kpak).normalize();
        }
    }
}

