/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.prng.EntropySource;

public interface EntropySourceProvider {
    public EntropySource get(int var1);
}

