/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.crypto.threshold.SecretShare;
import org.bouncycastle.crypto.threshold.SplitSecret;

public interface SecretSplitter {
    public SplitSecret split(int var1, int var2);

    public SplitSecret splitAround(SecretShare var1, int var2, int var3) throws IOException;

    public SplitSecret resplit(byte[] var1, int var2, int var3);
}

