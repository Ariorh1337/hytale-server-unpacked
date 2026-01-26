/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.drbg;

import org.bouncycastle.crypto.prng.EntropySource;

interface IncrementalEntropySource
extends EntropySource {
    public byte[] getEntropy(long var1) throws InterruptedException;
}

