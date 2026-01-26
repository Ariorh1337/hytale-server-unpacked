/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.crypto.engines.AsconBaseEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class AsconAEAD128
extends AsconBaseEngine {
    public AsconAEAD128() {
        this.BlockSize = 16;
        this.AADBufferSize = 16;
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        this.KEY_SIZE = 16;
        this.ASCON_IV = 17594342703105L;
        this.algorithmName = "Ascon-AEAD128";
        this.nr = 8;
        this.dsep = Long.MIN_VALUE;
        this.macSizeLowerBound = 4;
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.DataLimit, AEADBaseEngine.DataOperatorType.DataLimit);
        this.dataLimitCounter.init(54);
        this.decryptionFailureCounter = new AEADBaseEngine.DecryptionFailureCounter();
    }

    @Override
    protected long pad(int n) {
        return 1L << (n << 3);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n) {
        return Pack.littleEndianToLong(byArray, n);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n) {
        Pack.longToLittleEndian(l, byArray, n);
    }

    @Override
    protected void ascon_aeadinit() {
        this.p.set(this.ASCON_IV, this.K0, this.K1, this.N0, this.N1);
        this.p.p(12);
        this.p.x3 ^= this.K0;
        this.p.x4 ^= this.K1;
    }

    @Override
    protected void processFinalAAD() {
        if (this.m_aadPos == this.BlockSize) {
            this.p.x0 ^= this.loadBytes(this.m_aad, 0);
            this.p.x1 ^= this.loadBytes(this.m_aad, 8);
            this.m_aadPos -= this.BlockSize;
            this.p.p(this.nr);
        }
        Arrays.fill(this.m_aad, this.m_aadPos, this.AADBufferSize, (byte)0);
        if (this.m_aadPos >= 8) {
            this.p.x0 ^= Pack.littleEndianToLong(this.m_aad, 0);
            this.p.x1 ^= Pack.littleEndianToLong(this.m_aad, 8) ^ this.pad(this.m_aadPos);
        } else {
            this.p.x0 ^= Pack.littleEndianToLong(this.m_aad, 0) ^ this.pad(this.m_aadPos);
        }
    }

    @Override
    protected void processFinalDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (n >= 8) {
            long l = Pack.littleEndianToLong(byArray, 0);
            long l2 = Pack.littleEndianToLong(byArray, 8, n -= 8);
            Pack.longToLittleEndian(this.p.x0 ^ l, byArray2, n2);
            Pack.longToLittleEndian(this.p.x1 ^ l2, byArray2, n2 + 8, n);
            this.p.x0 = l;
            this.p.x1 &= -(1L << (n << 3));
            this.p.x1 |= l2;
            this.p.x1 ^= this.pad(n);
        } else {
            if (n != 0) {
                long l = Pack.littleEndianToLong(byArray, 0, n);
                Pack.longToLittleEndian(this.p.x0 ^ l, byArray2, n2, n);
                this.p.x0 &= -(1L << (n << 3));
                this.p.x0 |= l;
            }
            this.p.x0 ^= this.pad(n);
        }
        this.finishData(AEADBaseEngine.State.DecFinal);
    }

    @Override
    protected void processFinalEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (n >= 8) {
            this.p.x0 ^= Pack.littleEndianToLong(byArray, 0);
            this.p.x1 ^= Pack.littleEndianToLong(byArray, 8, n -= 8);
            Pack.longToLittleEndian(this.p.x0, byArray2, n2);
            Pack.longToLittleEndian(this.p.x1, byArray2, n2 + 8);
            this.p.x1 ^= this.pad(n);
        } else {
            if (n != 0) {
                this.p.x0 ^= Pack.littleEndianToLong(byArray, 0, n);
                Pack.longToLittleEndian(this.p.x0, byArray2, n2, n);
            }
            this.p.x0 ^= this.pad(n);
        }
        this.finishData(AEADBaseEngine.State.EncFinal);
    }

    private void finishData(AEADBaseEngine.State state) {
        this.p.x2 ^= this.K0;
        this.p.x3 ^= this.K1;
        this.p.p(12);
        this.p.x3 ^= this.K0;
        this.p.x4 ^= this.K1;
        this.m_state = state;
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        int n = (this.MAC_SIZE << 3) - 32;
        long l = Pack.littleEndianToLong(byArray, 0);
        long l2 = Pack.littleEndianToLong(byArray, 8);
        this.decryptionFailureCounter.init(n);
        if (this.K0 != l || this.K1 != l2) {
            this.dataLimitCounter.reset();
            this.decryptionFailureCounter.reset();
            this.K0 = l;
            this.K1 = l2;
        }
        this.N0 = Pack.littleEndianToLong(byArray2, 0);
        this.N1 = Pack.littleEndianToLong(byArray2, 8);
    }

    @Override
    public String getAlgorithmVersion() {
        return "v1.3";
    }
}

