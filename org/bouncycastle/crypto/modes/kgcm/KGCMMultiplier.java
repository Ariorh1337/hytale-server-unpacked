/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

public interface KGCMMultiplier {
    public void init(long[] var1);

    public void multiplyH(long[] var1);
}

