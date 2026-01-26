/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.crypto.threshold.SecretShare;

public interface SplitSecret {
    public SecretShare[] getSecretShares();

    public byte[] getSecret() throws IOException;
}

