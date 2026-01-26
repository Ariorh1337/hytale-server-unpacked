/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAEngine;

interface SLHDSAEngineProvider {
    public int getN();

    public SLHDSAEngine get();
}

