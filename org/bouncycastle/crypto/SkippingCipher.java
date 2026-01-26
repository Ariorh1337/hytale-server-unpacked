/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public interface SkippingCipher {
    public long skip(long var1);

    public long seekTo(long var1);

    public long getPosition();
}

