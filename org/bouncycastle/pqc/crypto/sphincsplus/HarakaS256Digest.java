/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.sphincsplus.HarakaSBase;
import org.bouncycastle.pqc.crypto.sphincsplus.HarakaSXof;

class HarakaS256Digest
extends HarakaSBase
implements Digest {
    public HarakaS256Digest(HarakaSXof harakaSXof) {
        this.haraka256_rc = harakaSXof.haraka256_rc;
    }

    @Override
    public String getAlgorithmName() {
        return "HarakaS-256";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    public void update(byte by) {
        if (this.off > 31) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        this.buffer[this.off++] = by;
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        if (this.off > 32 - n2) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        System.arraycopy(byArray, n, this.buffer, this.off, n2);
        this.off += n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        byte[] byArray2 = new byte[32];
        this.haraka256Perm(byArray2);
        HarakaS256Digest.xor(byArray2, 0, this.buffer, 0, byArray, n, 32);
        this.reset();
        return byArray.length;
    }

    @Override
    public void reset() {
        super.reset();
    }
}

