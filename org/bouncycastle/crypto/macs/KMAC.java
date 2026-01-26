/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class KMAC
implements Mac,
Xof,
Memoable,
EncodableDigest {
    private static final byte[] padding = new byte[100];
    private final CSHAKEDigest cshake;
    private int bitLength;
    private int outputLength;
    private byte[] key;
    private boolean initialised;
    private boolean firstOutput;

    public KMAC(int n, byte[] byArray) {
        this.cshake = new CSHAKEDigest(n, Strings.toByteArray("KMAC"), byArray);
        this.bitLength = n;
        this.outputLength = n * 2 / 8;
    }

    public KMAC(KMAC kMAC) {
        this.cshake = new CSHAKEDigest(kMAC.cshake);
        this.bitLength = kMAC.bitLength;
        this.outputLength = kMAC.outputLength;
        this.key = kMAC.key;
        this.initialised = kMAC.initialised;
        this.firstOutput = kMAC.firstOutput;
    }

    public KMAC(byte[] byArray) {
        this.key = new byte[byArray[0] & 0xFF];
        System.arraycopy(byArray, 1, this.key, 0, this.key.length);
        this.cshake = new CSHAKEDigest(Arrays.copyOfRange(byArray, 1 + this.key.length, byArray.length - 10));
        this.bitLength = Pack.bigEndianToInt(byArray, byArray.length - 10);
        this.outputLength = Pack.bigEndianToInt(byArray, byArray.length - 6);
        this.initialised = byArray[byArray.length - 2] != 0;
        this.firstOutput = byArray[byArray.length - 1] != 0;
    }

    private void copyIn(KMAC kMAC) {
        this.cshake.reset(kMAC.cshake);
        this.bitLength = kMAC.bitLength;
        this.outputLength = kMAC.outputLength;
        this.initialised = kMAC.initialised;
        this.firstOutput = kMAC.firstOutput;
    }

    @Override
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        KeyParameter keyParameter = (KeyParameter)cipherParameters;
        this.key = Arrays.clone(keyParameter.getKey());
        if (this.key.length > 255) {
            throw new IllegalArgumentException("key length must be between 0 and 2040 bits");
        }
        this.initialised = true;
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "KMAC" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override
    public int getMacSize() {
        return this.outputLength;
    }

    @Override
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override
    public void update(byte by) throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
        }
        this.cshake.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) throws DataLengthException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
        }
        this.cshake.update(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] byArray2 = XofUtils.rightEncode(this.getMacSize() * 8);
            this.cshake.update(byArray2, 0, byArray2.length);
        }
        int n2 = this.cshake.doFinal(byArray, n, this.getMacSize());
        this.reset();
        return n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n, int n2) {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] byArray2 = XofUtils.rightEncode(n2 * 8);
            this.cshake.update(byArray2, 0, byArray2.length);
        }
        int n3 = this.cshake.doFinal(byArray, n, n2);
        this.reset();
        return n3;
    }

    @Override
    public int doOutput(byte[] byArray, int n, int n2) {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] byArray2 = XofUtils.rightEncode(0L);
            this.cshake.update(byArray2, 0, byArray2.length);
            this.firstOutput = false;
        }
        return this.cshake.doOutput(byArray, n, n2);
    }

    @Override
    public void reset() {
        this.cshake.reset();
        if (this.key != null) {
            if (this.bitLength == 128) {
                this.bytePad(this.key, 168);
            } else {
                this.bytePad(this.key, 136);
            }
        }
        this.firstOutput = true;
    }

    private void bytePad(byte[] byArray, int n) {
        int n2;
        byte[] byArray2 = XofUtils.leftEncode(n);
        this.update(byArray2, 0, byArray2.length);
        byte[] byArray3 = KMAC.encode(byArray);
        this.update(byArray3, 0, byArray3.length);
        if (n2 > 0 && n2 != n) {
            for (n2 = n - (byArray2.length + byArray3.length) % n; n2 > padding.length; n2 -= padding.length) {
                this.update(padding, 0, padding.length);
            }
            this.update(padding, 0, n2);
        }
    }

    private static byte[] encode(byte[] byArray) {
        return Arrays.concatenate(XofUtils.leftEncode(byArray.length * 8), byArray);
    }

    @Override
    public byte[] getEncodedState() {
        if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialised");
        }
        byte[] byArray = this.cshake.getEncodedState();
        byte[] byArray2 = new byte[10];
        Pack.intToBigEndian(this.bitLength, byArray2, 0);
        Pack.intToBigEndian(this.outputLength, byArray2, 4);
        byArray2[8] = this.initialised ? (byte)1 : 0;
        byArray2[9] = this.firstOutput ? (byte)1 : 0;
        byte[] byArray3 = new byte[1 + this.key.length + byArray.length + byArray2.length];
        byArray3[0] = (byte)this.key.length;
        System.arraycopy(this.key, 0, byArray3, 1, this.key.length);
        System.arraycopy(byArray, 0, byArray3, 1 + this.key.length, byArray.length);
        System.arraycopy(byArray2, 0, byArray3, 1 + this.key.length + byArray.length, byArray2.length);
        return byArray3;
    }

    @Override
    public Memoable copy() {
        return new KMAC(this);
    }

    @Override
    public void reset(Memoable memoable) {
        this.copyIn((KMAC)memoable);
    }
}

