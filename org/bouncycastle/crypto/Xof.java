/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.ExtendedDigest;

public interface Xof
extends ExtendedDigest {
    public int doFinal(byte[] var1, int var2, int var3);

    public int doOutput(byte[] var1, int var2, int var3);
}

