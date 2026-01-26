/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class TupleHash
implements Xof,
SavableDigest {
    private static final byte[] N_TUPLE_HASH = Strings.toByteArray("TupleHash");
    private final CSHAKEDigest cshake;
    private int bitLength;
    private int outputLength;
    private boolean firstOutput;

    public TupleHash(int n, byte[] byArray) {
        this(n, byArray, n * 2);
    }

    public TupleHash(int n, byte[] byArray, int n2) {
        this.cshake = new CSHAKEDigest(n, N_TUPLE_HASH, byArray);
        this.bitLength = n;
        this.outputLength = (n2 + 7) / 8;
        this.reset();
    }

    public TupleHash(TupleHash tupleHash) {
        this.cshake = new CSHAKEDigest(tupleHash.cshake);
        this.bitLength = tupleHash.bitLength;
        this.outputLength = tupleHash.outputLength;
        this.firstOutput = tupleHash.firstOutput;
    }

    public TupleHash(byte[] byArray) {
        this.cshake = new CSHAKEDigest(Arrays.copyOfRange(byArray, 0, byArray.length - 9));
        this.bitLength = Pack.bigEndianToInt(byArray, byArray.length - 9);
        this.outputLength = Pack.bigEndianToInt(byArray, byArray.length - 5);
        this.firstOutput = byArray[byArray.length - 1] != 0;
    }

    private void copyIn(TupleHash tupleHash) {
        this.cshake.reset(tupleHash.cshake);
        this.bitLength = this.cshake.fixedOutputLength;
        this.outputLength = this.bitLength * 2 / 8;
        this.firstOutput = tupleHash.firstOutput;
    }

    @Override
    public String getAlgorithmName() {
        return "TupleHash" + this.cshake.getAlgorithmName().substring(6);
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
        byte[] byArray = XofUtils.encode(by);
        this.cshake.update(byArray, 0, byArray.length);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) throws DataLengthException, IllegalStateException {
        byte[] byArray2 = XofUtils.encode(byArray, n, n2);
        this.cshake.update(byArray2, 0, byArray2.length);
    }

    private void wrapUp(int n) {
        byte[] byArray = XofUtils.rightEncode((long)n * 8L);
        this.cshake.update(byArray, 0, byArray.length);
        this.firstOutput = false;
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            this.wrapUp(this.getDigestSize());
        }
        int n2 = this.cshake.doFinal(byArray, n, this.getDigestSize());
        this.reset();
        return n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n, int n2) {
        if (this.firstOutput) {
            this.wrapUp(this.getDigestSize());
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
        this.firstOutput = true;
    }

    @Override
    public byte[] getEncodedState() {
        byte[] byArray = this.cshake.getEncodedState();
        byte[] byArray2 = new byte[9];
        Pack.intToBigEndian(this.bitLength, byArray2, 0);
        Pack.intToBigEndian(this.outputLength, byArray2, 4);
        byArray2[8] = this.firstOutput ? (byte)1 : 0;
        return Arrays.concatenate(byArray, byArray2);
    }

    @Override
    public Memoable copy() {
        return new TupleHash(this);
    }

    @Override
    public void reset(Memoable memoable) {
        this.copyIn((TupleHash)memoable);
    }
}

