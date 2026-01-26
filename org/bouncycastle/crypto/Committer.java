/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.Commitment;

public interface Committer {
    public Commitment commit(byte[] var1);

    public boolean isRevealed(Commitment var1, byte[] var2);
}

