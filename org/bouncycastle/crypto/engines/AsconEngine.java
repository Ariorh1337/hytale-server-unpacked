/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.crypto.engines.AsconBaseEngine;
import org.bouncycastle.util.Pack;

public class AsconEngine
extends AsconBaseEngine {
    private final AsconParameters asconParameters;
    private long K2;

    public AsconEngine(AsconParameters asconParameters) {
        this.asconParameters = asconParameters;
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        switch (asconParameters.ordinal()) {
            case 0: {
                this.KEY_SIZE = 20;
                this.BlockSize = 8;
                this.ASCON_IV = -6899501409222262784L;
                this.algorithmName = "Ascon-80pq AEAD";
                break;
            }
            case 1: {
                this.KEY_SIZE = 16;
                this.BlockSize = 16;
                this.ASCON_IV = -9187330011336540160L;
                this.algorithmName = "Ascon-128a AEAD";
                break;
            }
            case 2: {
                this.KEY_SIZE = 16;
                this.BlockSize = 8;
                this.ASCON_IV = -9205344418435956736L;
                this.algorithmName = "Ascon-128 AEAD";
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid parameter setting for ASCON AEAD");
            }
        }
        this.nr = this.BlockSize == 8 ? 6 : 8;
        this.AADBufferSize = this.BlockSize;
        this.dsep = 1L;
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Default);
    }

    @Override
    protected long pad(int n) {
        return 128L << 56 - (n << 3);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n) {
        return Pack.bigEndianToLong(byArray, n);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n) {
        Pack.longToBigEndian(l, byArray, n);
    }

    @Override
    protected void ascon_aeadinit() {
        this.p.set(this.ASCON_IV, this.K1, this.K2, this.N0, this.N1);
        if (this.KEY_SIZE == 20) {
            this.p.x0 ^= this.K0;
        }
        this.p.p(12);
        if (this.KEY_SIZE == 20) {
            this.p.x2 ^= this.K0;
        }
        this.p.x3 ^= this.K1;
        this.p.x4 ^= this.K2;
    }

    @Override
    protected void processFinalAAD() {
        this.m_aad[this.m_aadPos] = -128;
        if (this.m_aadPos >= 8) {
            this.p.x0 ^= Pack.bigEndianToLong(this.m_aad, 0);
            this.p.x1 ^= Pack.bigEndianToLong(this.m_aad, 8) & -1L << 56 - (this.m_aadPos - 8 << 3);
        } else {
            this.p.x0 ^= Pack.bigEndianToLong(this.m_aad, 0) & -1L << 56 - (this.m_aadPos << 3);
        }
    }

    @Override
    protected void processFinalDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (n >= 8) {
            long l = Pack.bigEndianToLong(byArray, 0);
            this.p.x0 ^= l;
            Pack.longToBigEndian(this.p.x0, byArray2, n2);
            this.p.x0 = l;
            n2 += 8;
            this.p.x1 ^= this.pad(n -= 8);
            if (n != 0) {
                long l2 = Pack.littleEndianToLong_High(byArray, 8, n);
                this.p.x1 ^= l2;
                Pack.longToLittleEndian_High(this.p.x1, byArray2, n2, n);
                this.p.x1 &= -1L >>> (n << 3);
                this.p.x1 ^= l2;
            }
        } else {
            this.p.x0 ^= this.pad(n);
            if (n != 0) {
                long l = Pack.littleEndianToLong_High(byArray, 0, n);
                this.p.x0 ^= l;
                Pack.longToLittleEndian_High(this.p.x0, byArray2, n2, n);
                this.p.x0 &= -1L >>> (n << 3);
                this.p.x0 ^= l;
            }
        }
        this.finishData(AEADBaseEngine.State.DecFinal);
    }

    @Override
    protected void processFinalEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (n >= 8) {
            this.p.x0 ^= Pack.bigEndianToLong(byArray, 0);
            Pack.longToBigEndian(this.p.x0, byArray2, n2);
            n2 += 8;
            this.p.x1 ^= this.pad(n -= 8);
            if (n != 0) {
                this.p.x1 ^= Pack.littleEndianToLong_High(byArray, 8, n);
                Pack.longToLittleEndian_High(this.p.x1, byArray2, n2, n);
            }
        } else {
            this.p.x0 ^= this.pad(n);
            if (n != 0) {
                this.p.x0 ^= Pack.littleEndianToLong_High(byArray, 0, n);
                Pack.longToLittleEndian_High(this.p.x0, byArray2, n2, n);
            }
        }
        this.finishData(AEADBaseEngine.State.EncFinal);
    }

    protected void finishData(AEADBaseEngine.State state) {
        switch (this.asconParameters.ordinal()) {
            case 2: {
                this.p.x1 ^= this.K1;
                this.p.x2 ^= this.K2;
                break;
            }
            case 1: {
                this.p.x2 ^= this.K1;
                this.p.x3 ^= this.K2;
                break;
            }
            case 0: {
                this.p.x1 ^= this.K0 << 32 | this.K1 >> 32;
                this.p.x2 ^= this.K1 << 32 | this.K2 >> 32;
                this.p.x3 ^= this.K2 << 32;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.p.p(12);
        this.p.x3 ^= this.K1;
        this.p.x4 ^= this.K2;
        this.m_state = state;
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.N0 = Pack.bigEndianToLong(byArray2, 0);
        this.N1 = Pack.bigEndianToLong(byArray2, 8);
        if (this.KEY_SIZE == 16) {
            this.K1 = Pack.bigEndianToLong(byArray, 0);
            this.K2 = Pack.bigEndianToLong(byArray, 8);
        } else if (this.KEY_SIZE == 20) {
            this.K0 = Pack.bigEndianToInt(byArray, 0);
            this.K1 = Pack.bigEndianToLong(byArray, 4);
            this.K2 = Pack.bigEndianToLong(byArray, 12);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAlgorithmVersion() {
        return "v1.2";
    }

    public static enum AsconParameters {
        ascon80pq,
        ascon128a,
        ascon128;

    }
}

