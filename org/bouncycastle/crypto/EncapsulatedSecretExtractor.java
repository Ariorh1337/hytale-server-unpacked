/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public interface EncapsulatedSecretExtractor {
    public byte[] extractSecret(byte[] var1);

    public int getEncapsulationLength();
}

