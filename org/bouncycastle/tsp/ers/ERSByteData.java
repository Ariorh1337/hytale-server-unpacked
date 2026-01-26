/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSByteData
extends ERSCachingData {
    private final byte[] content;

    public ERSByteData(byte[] byArray) {
        this.content = byArray;
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] byArray) {
        byte[] byArray2 = ERSUtil.calculateDigest(digestCalculator, this.content);
        if (byArray != null) {
            return ERSUtil.concatPreviousHashes(digestCalculator, byArray, byArray2);
        }
        return byArray2;
    }
}

