/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;

public interface ERSData {
    public byte[] getHash(DigestCalculator var1, byte[] var2);
}

