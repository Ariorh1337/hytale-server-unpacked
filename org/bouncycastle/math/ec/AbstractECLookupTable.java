/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;

public abstract class AbstractECLookupTable
implements ECLookupTable {
    @Override
    public ECPoint lookupVar(int n) {
        return this.lookup(n);
    }
}

