/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.pqc.crypto.falcon.FalconRNG;

class SamplerCtx {
    double sigma_min = 0.0;
    FalconRNG p = new FalconRNG();

    SamplerCtx() {
    }
}

