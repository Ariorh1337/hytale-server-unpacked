/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ParallelHash
implements Xof,
Digest {
    private static final byte[] N_PARALLEL_HASH = Strings.toByteArray("ParallelHash");
    private final CSHAKEDigest cshake;
    private final CSHAKEDigest compressor;
    private final int bitLength;
    private final int outputLength;
    private final int B;
    private final byte[] buffer;
    private final byte[] compressorBuffer;
    private boolean firstOutput;
    private int nCount;
    private int bufOff;
    private final CryptoServicePurpose purpose;

    public ParallelHash(int n, byte[] byArray, int n2) {
        this(n, byArray, n2, n * 2, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int n, byte[] byArray, int n2, int n3) {
        this(n, byArray, n2, n3, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int n, byte[] byArray, int n2, int n3, CryptoServicePurpose cryptoServicePurpose) {
        if (n2 <= 0) {
            throw new IllegalArgumentException("block size should be greater than 0");
        }
        this.cshake = new CSHAKEDigest(n, N_PARALLEL_HASH, byArray);
        this.compressor = new CSHAKEDigest(n, new byte[0], new byte[0]);
        this.bitLength = n;
        this.B = n2;
        this.outputLength = (n3 + 7) / 8;
        this.buffer = new byte[n2];
        this.compressorBuffer = new byte[n * 2 / 8];
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, n, cryptoServicePurpose));
        this.reset();
    }

    public ParallelHash(ParallelHash parallelHash) {
        this.cshake = new CSHAKEDigest(parallelHash.cshake);
        this.compressor = new CSHAKEDigest(parallelHash.compressor);
        this.bitLength = parallelHash.bitLength;
        this.B = parallelHash.B;
        this.outputLength = parallelHash.outputLength;
        this.buffer = Arrays.clone(parallelHash.buffer);
        this.compressorBuffer = Arrays.clone(parallelHash.compressorBuffer);
        this.purpose = parallelHash.purpose;
        this.firstOutput = parallelHash.firstOutput;
        this.nCount = parallelHash.nCount;
        this.bufOff = parallelHash.bufOff;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.bitLength, this.purpose));
    }

    @Override
    public String getAlgorithmName() {
        return "ParallelHash" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override
    public void update(byte by) throws IllegalStateException {
        this.buffer[this.bufOff++] = by;
        if (this.bufOff == this.buffer.length) {
            this.compress();
        }
    }

    @Override
    public void update(byte[] byArray, int n, int n2) throws DataLengthException, IllegalStateException {
        n2 = Math.max(0, n2);
        int n3 = 0;
        if (this.bufOff != 0) {
            while (n3 < n2 && this.bufOff != this.buffer.length) {
                this.buffer[this.bufOff++] = byArray[n + n3++];
            }
            if (this.bufOff == this.buffer.length) {
                this.compress();
            }
        }
        if (n3 < n2) {
            while (n2 - n3 >= this.B) {
                this.compress(byArray, n + n3, this.B);
                n3 += this.B;
            }
        }
        while (n3 < n2) {
            this.update(byArray[n + n3++]);
        }
    }

    private void compress() {
        this.compress(this.buffer, 0, this.bufOff);
        this.bufOff = 0;
    }

    private void compress(byte[] byArray, int n, int n2) {
        this.compressor.update(byArray, n, n2);
        this.compressor.doFinal(this.compressorBuffer, 0, this.compressorBuffer.length);
        this.cshake.update(this.compressorBuffer, 0, this.compressorBuffer.length);
        ++this.nCount;
    }

    private void wrapUp(int n) {
        if (this.bufOff != 0) {
            this.compress();
        }
        byte[] byArray = XofUtils.rightEncode(this.nCount);
        byte[] byArray2 = XofUtils.rightEncode(n * 8);
        this.cshake.update(byArray, 0, byArray.length);
        this.cshake.update(byArray2, 0, byArray2.length);
        this.firstOutput = false;
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            this.wrapUp(this.outputLength);
        }
        int n2 = this.cshake.doFinal(byArray, n, this.getDigestSize());
        this.reset();
        return n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n, int n2) {
        if (this.firstOutput) {
            this.wrapUp(this.outputLength);
        }
        int n3 = this.cshake.doFinal(byArray, n, n2);
        this.reset();
        return n3;
    }

    @Override
    public int doOutput(byte[] byArray, int n, int n2) {
        if (this.firstOutput) {
            this.wrapUp(0);
        }
        return this.cshake.doOutput(byArray, n, n2);
    }

    @Override
    public void reset() {
        this.cshake.reset();
        Arrays.clear(this.buffer);
        byte[] byArray = XofUtils.leftEncode(this.B);
        this.cshake.update(byArray, 0, byArray.length);
        this.nCount = 0;
        this.bufOff = 0;
        this.firstOutput = true;
    }
}

