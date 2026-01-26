/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.pqc.crypto.snova.SnovaParameters;

class MapGroup2 {
    public final byte[][][][] f11;
    public final byte[][][][] f12;
    public final byte[][][][] f21;

    public MapGroup2(SnovaParameters snovaParameters) {
        int n = snovaParameters.getM();
        int n2 = snovaParameters.getV();
        int n3 = snovaParameters.getO();
        int n4 = snovaParameters.getLsq();
        this.f11 = new byte[n][n2][n2][n4];
        this.f12 = new byte[n][n2][n3][n4];
        this.f21 = new byte[n][n3][n2][n4];
    }
}

