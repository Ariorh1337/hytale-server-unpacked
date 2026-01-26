/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

interface DRBGProvider {
    public String getAlgorithm();

    public SP80090DRBG get(EntropySource var1);
}

