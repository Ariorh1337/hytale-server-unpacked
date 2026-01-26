/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;

public interface MessageSigner {
    public void init(boolean var1, CipherParameters var2);

    public byte[] generateSignature(byte[] var1);

    public boolean verifySignature(byte[] var1, byte[] var2);
}

