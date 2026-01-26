/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.pqc.crypto.picnic.Utils;

class Signature {
    final byte[] challengeBits;
    final byte[] salt = new byte[32];
    final Proof[] proofs;

    Signature(PicnicEngine picnicEngine) {
        this.challengeBits = new byte[Utils.numBytes(picnicEngine.numMPCRounds * 2)];
        this.proofs = new Proof[picnicEngine.numMPCRounds];
        for (int i = 0; i < this.proofs.length; ++i) {
            this.proofs[i] = new Proof(picnicEngine);
        }
    }

    public static class Proof {
        final byte[] seed1;
        final byte[] seed2;
        final int[] inputShare;
        final byte[] communicatedBits;
        final byte[] view3Commitment;
        final byte[] view3UnruhG;

        Proof(PicnicEngine picnicEngine) {
            this.seed1 = new byte[picnicEngine.seedSizeBytes];
            this.seed2 = new byte[picnicEngine.seedSizeBytes];
            this.inputShare = new int[picnicEngine.stateSizeWords];
            this.communicatedBits = new byte[picnicEngine.andSizeBytes];
            this.view3Commitment = new byte[picnicEngine.digestSizeBytes];
            this.view3UnruhG = (byte[])(picnicEngine.UnruhGWithInputBytes > 0 ? new byte[picnicEngine.UnruhGWithInputBytes] : null);
        }
    }
}

