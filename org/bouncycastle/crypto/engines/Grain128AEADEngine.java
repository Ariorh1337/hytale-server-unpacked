/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Grain128AEADEngine
extends AEADBaseEngine {
    private static final int STATE_SIZE = 4;
    private byte[] workingKey;
    private byte[] workingIV;
    private final int[] lfsr;
    private final int[] nfsr;
    private final int[] authAcc;
    private final int[] authSr;

    public Grain128AEADEngine() {
        this.algorithmName = "Grain-128 AEAD";
        this.KEY_SIZE = 16;
        this.IV_SIZE = 12;
        this.MAC_SIZE = 8;
        this.lfsr = new int[4];
        this.nfsr = new int[4];
        this.authAcc = new int[2];
        this.authSr = new int[2];
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Stream, AEADBaseEngine.DataOperatorType.StreamCipher);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.workingIV = new byte[16];
        this.workingKey = byArray;
        System.arraycopy(byArray2, 0, this.workingIV, 0, this.IV_SIZE);
        this.workingIV[12] = -1;
        this.workingIV[13] = -1;
        this.workingIV[14] = -1;
        this.workingIV[15] = 127;
    }

    private void initGrain(int[] nArray) {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 32; ++j) {
                int n = i;
                nArray[n] = nArray[n] | this.getByteKeyStream() << j;
            }
        }
    }

    private int getOutputNFSR() {
        int n = this.nfsr[0];
        int n2 = this.nfsr[0] >>> 3;
        int n3 = this.nfsr[0] >>> 11;
        int n4 = this.nfsr[0] >>> 13;
        int n5 = this.nfsr[0] >>> 17;
        int n6 = this.nfsr[0] >>> 18;
        int n7 = this.nfsr[0] >>> 22;
        int n8 = this.nfsr[0] >>> 24;
        int n9 = this.nfsr[0] >>> 25;
        int n10 = this.nfsr[0] >>> 26;
        int n11 = this.nfsr[0] >>> 27;
        int n12 = this.nfsr[1] >>> 8;
        int n13 = this.nfsr[1] >>> 16;
        int n14 = this.nfsr[1] >>> 24;
        int n15 = this.nfsr[1] >>> 27;
        int n16 = this.nfsr[1] >>> 29;
        int n17 = this.nfsr[2] >>> 1;
        int n18 = this.nfsr[2] >>> 3;
        int n19 = this.nfsr[2] >>> 4;
        int n20 = this.nfsr[2] >>> 6;
        int n21 = this.nfsr[2] >>> 14;
        int n22 = this.nfsr[2] >>> 18;
        int n23 = this.nfsr[2] >>> 20;
        int n24 = this.nfsr[2] >>> 24;
        int n25 = this.nfsr[2] >>> 27;
        int n26 = this.nfsr[2] >>> 28;
        int n27 = this.nfsr[2] >>> 29;
        int n28 = this.nfsr[2] >>> 31;
        int n29 = this.nfsr[3];
        return (n ^ n10 ^ n14 ^ n25 ^ n29 ^ n2 & n18 ^ n3 & n4 ^ n5 & n6 ^ n11 & n15 ^ n12 & n13 ^ n16 & n17 ^ n19 & n23 ^ n7 & n8 & n9 ^ n20 & n21 & n22 ^ n24 & n26 & n27 & n28) & 1;
    }

    private int getOutputLFSR() {
        int n = this.lfsr[0];
        int n2 = this.lfsr[0] >>> 7;
        int n3 = this.lfsr[1] >>> 6;
        int n4 = this.lfsr[2] >>> 6;
        int n5 = this.lfsr[2] >>> 17;
        int n6 = this.lfsr[3];
        return (n ^ n2 ^ n3 ^ n4 ^ n5 ^ n6) & 1;
    }

    private int getOutput() {
        int n = this.nfsr[0] >>> 2;
        int n2 = this.nfsr[0] >>> 12;
        int n3 = this.nfsr[0] >>> 15;
        int n4 = this.nfsr[1] >>> 4;
        int n5 = this.nfsr[1] >>> 13;
        int n6 = this.nfsr[2];
        int n7 = this.nfsr[2] >>> 9;
        int n8 = this.nfsr[2] >>> 25;
        int n9 = this.nfsr[2] >>> 31;
        int n10 = this.lfsr[0] >>> 8;
        int n11 = this.lfsr[0] >>> 13;
        int n12 = this.lfsr[0] >>> 20;
        int n13 = this.lfsr[1] >>> 10;
        int n14 = this.lfsr[1] >>> 28;
        int n15 = this.lfsr[2] >>> 15;
        int n16 = this.lfsr[2] >>> 29;
        int n17 = this.lfsr[2] >>> 30;
        return (n2 & n10 ^ n11 & n12 ^ n9 & n13 ^ n14 & n15 ^ n2 & n9 & n17 ^ n16 ^ n ^ n3 ^ n4 ^ n5 ^ n6 ^ n7 ^ n8) & 1;
    }

    private void shift(int[] nArray, int n) {
        nArray[0] = nArray[0] >>> 1 | nArray[1] << 31;
        nArray[1] = nArray[1] >>> 1 | nArray[2] << 31;
        nArray[2] = nArray[2] >>> 1 | nArray[3] << 31;
        nArray[3] = nArray[3] >>> 1 | n << 31;
    }

    private void shift() {
        this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
        this.shift(this.lfsr, this.getOutputLFSR() & 1);
    }

    @Override
    protected void reset(boolean bl) {
        int n;
        int n2;
        super.reset(bl);
        Pack.littleEndianToInt(this.workingKey, 0, this.nfsr);
        Pack.littleEndianToInt(this.workingIV, 0, this.lfsr);
        Arrays.clear(this.authAcc);
        Arrays.clear(this.authSr);
        for (n2 = 0; n2 < 320; ++n2) {
            n = this.getOutput();
            this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ n) & 1);
            this.shift(this.lfsr, (this.getOutputLFSR() ^ n) & 1);
        }
        for (n2 = 0; n2 < 8; ++n2) {
            for (int i = 0; i < 8; ++i) {
                n = this.getOutput();
                this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ n ^ this.workingKey[n2] >> i) & 1);
                this.shift(this.lfsr, (this.getOutputLFSR() ^ n ^ this.workingKey[n2 + 8] >> i) & 1);
            }
        }
        this.initGrain(this.authAcc);
        this.initGrain(this.authSr);
    }

    private void updateInternalState(int n) {
        n = -n;
        this.authAcc[0] = this.authAcc[0] ^ this.authSr[0] & n;
        this.authAcc[1] = this.authAcc[1] ^ this.authSr[1] & n;
        n = this.getByteKeyStream();
        this.authSr[0] = this.authSr[0] >>> 1 | this.authSr[1] << 31;
        this.authSr[1] = this.authSr[1] >>> 1 | n << 31;
    }

    @Override
    public int getUpdateOutputSize(int n) {
        return this.getTotalBytesForUpdate(n);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD1(state);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        this.authAcc[0] = this.authAcc[0] ^ this.authSr[0];
        this.authAcc[1] = this.authAcc[1] ^ this.authSr[1];
        Pack.intToLittleEndian(this.authAcc, this.mac, 0);
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
    }

    @Override
    protected void processFinalAAD() {
        int n;
        int n2 = this.aadOperator.getLen();
        byte[] byArray = ((AEADBaseEngine.StreamAADOperator)this.aadOperator).getBytes();
        byte[] byArray2 = new byte[5];
        if (n2 < 128) {
            n = byArray2.length - 1;
            byArray2[n] = (byte)n2;
        } else {
            n = byArray2.length;
            int n3 = n2;
            do {
                byArray2[--n] = (byte)n3;
            } while ((n3 >>>= 8) != 0);
            int n4 = byArray2.length - n;
            byArray2[--n] = (byte)(0x80 | n4);
        }
        this.absorbAadData(byArray2, n, byArray2.length - n);
        this.absorbAadData(byArray, 0, n2);
    }

    private void absorbAadData(byte[] byArray, int n, int n2) {
        for (int i = 0; i < n2; ++i) {
            byte by = byArray[n + i];
            for (int j = 0; j < 8; ++j) {
                this.shift();
                this.updateInternalState(by >> j & 1);
            }
        }
    }

    private int getByteKeyStream() {
        int n = this.getOutput();
        this.shift();
        return n;
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        int n3 = this.dataOperator.getLen();
        for (int i = 0; i < n3; ++i) {
            byte by = 0;
            byte by2 = byArray[n + i];
            for (int j = 0; j < 8; ++j) {
                int n4 = by2 >> j & 1;
                by = (byte)(by | (n4 ^ this.getByteKeyStream()) << j);
                this.updateInternalState(n4);
            }
            byArray2[n2 + i] = by;
        }
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        int n3 = this.dataOperator.getLen();
        for (int i = 0; i < n3; ++i) {
            byte by = 0;
            byte by2 = byArray[n + i];
            for (int j = 0; j < 8; ++j) {
                by = (byte)(by | (by2 >> j & 1 ^ this.getByteKeyStream()) << j);
                this.updateInternalState(by >> j & 1);
            }
            byArray2[n2 + i] = by;
        }
    }
}

