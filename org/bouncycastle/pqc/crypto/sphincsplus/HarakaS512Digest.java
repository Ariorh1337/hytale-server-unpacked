/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.sphincsplus.HarakaSBase;
import org.bouncycastle.pqc.crypto.sphincsplus.HarakaSXof;

class HarakaS512Digest
extends HarakaSBase
implements Digest {
    public HarakaS512Digest(HarakaSXof harakaSXof) {
        this.haraka512_rc = harakaSXof.haraka512_rc;
    }

    @Override
    public String getAlgorithmName() {
        return "HarakaS-512";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    public void update(byte by) {
        if (this.off > 63) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        this.buffer[this.off++] = by;
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        if (this.off > 64 - n2) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        System.arraycopy(byArray, n, this.buffer, this.off, n2);
        this.off += n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        byte[] byArray2 = new byte[64];
        this.haraka512Perm(byArray2);
        HarakaS512Digest.xor(byArray2, 8, this.buffer, 8, byArray, n, 8);
        HarakaS512Digest.xor(byArray2, 24, this.buffer, 24, byArray, n + 8, 16);
        HarakaS512Digest.xor(byArray2, 48, this.buffer, 48, byArray, n + 24, 8);
        this.reset();
        return byArray2.length;
    }

    @Override
    public void reset() {
        super.reset();
    }
}

