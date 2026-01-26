/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

public interface EntropySource {
    public boolean isPredictionResistant();

    public byte[] getEntropy();

    public int entropySize();
}

