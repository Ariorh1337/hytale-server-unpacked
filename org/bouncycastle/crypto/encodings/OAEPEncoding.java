/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class OAEPEncoding
implements AsymmetricBlockCipher {
    private final AsymmetricBlockCipher engine;
    private final Digest mgf1Hash;
    private final int mgf1NoMemoLimit;
    private final byte[] defHash;
    private SecureRandom random;
    private boolean forEncryption;

    private static int getMGF1NoMemoLimit(Digest digest) {
        if (digest instanceof Memoable && digest instanceof ExtendedDigest) {
            return ((ExtendedDigest)digest).getByteLength() - 1;
        }
        return Integer.MAX_VALUE;
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this(asymmetricBlockCipher, DigestFactory.createSHA1(), null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest) {
        this(asymmetricBlockCipher, digest, null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, byte[] byArray) {
        this(asymmetricBlockCipher, digest, digest, byArray);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, Digest digest2, byte[] byArray) {
        this.engine = asymmetricBlockCipher;
        this.mgf1Hash = digest2;
        this.mgf1NoMemoLimit = OAEPEncoding.getMGF1NoMemoLimit(digest2);
        this.defHash = new byte[digest.getDigestSize()];
        digest.reset();
        if (byArray != null) {
            digest.update(byArray, 0, byArray.length);
        }
        digest.doFinal(this.defHash, 0);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        SecureRandom secureRandom = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            secureRandom = parametersWithRandom.getRandom();
        }
        this.random = bl ? CryptoServicesRegistrar.getSecureRandom(secureRandom) : null;
        this.forEncryption = bl;
        this.engine.init(bl, cipherParameters);
    }

    @Override
    public int getInputBlockSize() {
        int n = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return n - 1 - 2 * this.defHash.length;
        }
        return n;
    }

    @Override
    public int getOutputBlockSize() {
        int n = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return n;
        }
        return n - 1 - 2 * this.defHash.length;
    }

    @Override
    public byte[] processBlock(byte[] byArray, int n, int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(byArray, n, n2);
        }
        return this.decodeBlock(byArray, n, n2);
    }

    public byte[] encodeBlock(byte[] byArray, int n, int n2) throws InvalidCipherTextException {
        int n3 = this.getInputBlockSize();
        if (n2 > n3) {
            throw new DataLengthException("input data too long");
        }
        byte[] byArray2 = new byte[n3 + 1 + 2 * this.defHash.length];
        System.arraycopy(byArray, n, byArray2, byArray2.length - n2, n2);
        byArray2[byArray2.length - n2 - 1] = 1;
        System.arraycopy(this.defHash, 0, byArray2, this.defHash.length, this.defHash.length);
        byte[] byArray3 = new byte[this.defHash.length];
        this.random.nextBytes(byArray3);
        System.arraycopy(byArray3, 0, byArray2, 0, this.defHash.length);
        this.mgf1Hash.reset();
        this.maskGeneratorFunction1(byArray3, 0, byArray3.length, byArray2, this.defHash.length, byArray2.length - this.defHash.length);
        this.maskGeneratorFunction1(byArray2, this.defHash.length, byArray2.length - this.defHash.length, byArray2, 0, this.defHash.length);
        return this.engine.processBlock(byArray2, 0, byArray2.length);
    }

    public byte[] decodeBlock(byte[] byArray, int n, int n2) throws InvalidCipherTextException {
        int n3;
        int n4 = this.getOutputBlockSize() >> 31;
        byte[] byArray2 = new byte[this.engine.getOutputBlockSize()];
        byte[] byArray3 = this.engine.processBlock(byArray, n, n2);
        n4 |= byArray2.length - byArray3.length >> 31;
        int n5 = Math.min(byArray2.length, byArray3.length);
        System.arraycopy(byArray3, 0, byArray2, byArray2.length - n5, n5);
        Arrays.fill(byArray3, (byte)0);
        this.mgf1Hash.reset();
        this.maskGeneratorFunction1(byArray2, this.defHash.length, byArray2.length - this.defHash.length, byArray2, 0, this.defHash.length);
        this.maskGeneratorFunction1(byArray2, 0, this.defHash.length, byArray2, this.defHash.length, byArray2.length - this.defHash.length);
        for (n3 = 0; n3 != this.defHash.length; ++n3) {
            n4 |= this.defHash[n3] ^ byArray2[this.defHash.length + n3];
        }
        n3 = -1;
        for (n5 = 2 * this.defHash.length; n5 != byArray2.length; ++n5) {
            int n6 = byArray2[n5] & 0xFF;
            int n7 = (-n6 & n3) >> 31;
            n3 += n5 & n7;
        }
        n4 |= n3 >> 31;
        if ((n4 |= byArray2[++n3] ^ 1) != 0) {
            Arrays.fill(byArray2, (byte)0);
            throw new InvalidCipherTextException("data wrong");
        }
        byte[] byArray4 = new byte[byArray2.length - ++n3];
        System.arraycopy(byArray2, n3, byArray4, 0, byArray4.length);
        Arrays.fill(byArray2, (byte)0);
        return byArray4;
    }

    private void maskGeneratorFunction1(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        int n5;
        int n6 = this.mgf1Hash.getDigestSize();
        byte[] byArray3 = new byte[n6];
        byte[] byArray4 = new byte[4];
        int n7 = 0;
        int n8 = n3 + n4;
        int n9 = n8 - n6;
        this.mgf1Hash.update(byArray, n, n2);
        if (n2 > this.mgf1NoMemoLimit) {
            Memoable memoable = (Memoable)((Object)this.mgf1Hash);
            Memoable memoable2 = memoable.copy();
            for (n5 = n3; n5 < n9; n5 += n6) {
                Pack.intToBigEndian(n7++, byArray4, 0);
                this.mgf1Hash.update(byArray4, 0, byArray4.length);
                this.mgf1Hash.doFinal(byArray3, 0);
                memoable.reset(memoable2);
                Bytes.xorTo(n6, byArray3, 0, byArray2, n5);
            }
        } else {
            while (n5 < n9) {
                Pack.intToBigEndian(n7++, byArray4, 0);
                this.mgf1Hash.update(byArray4, 0, byArray4.length);
                this.mgf1Hash.doFinal(byArray3, 0);
                this.mgf1Hash.update(byArray, n, n2);
                Bytes.xorTo(n6, byArray3, 0, byArray2, n5);
                n5 += n6;
            }
        }
        Pack.intToBigEndian(n7, byArray4, 0);
        this.mgf1Hash.update(byArray4, 0, byArray4.length);
        this.mgf1Hash.doFinal(byArray3, 0);
        Bytes.xorTo(n8 - n5, byArray3, 0, byArray2, n5);
    }
}

