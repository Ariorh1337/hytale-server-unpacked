/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECPoint;

public interface ECLookupTable {
    public int getSize();

    public ECPoint lookup(int var1);

    public ECPoint lookupVar(int var1);
}

