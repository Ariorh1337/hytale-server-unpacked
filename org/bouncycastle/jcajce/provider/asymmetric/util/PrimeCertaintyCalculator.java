/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

public class PrimeCertaintyCalculator {
    private PrimeCertaintyCalculator() {
    }

    public static int getDefaultCertainty(int n) {
        return n <= 1024 ? 80 : 96 + 16 * ((n - 1) / 1024);
    }
}

