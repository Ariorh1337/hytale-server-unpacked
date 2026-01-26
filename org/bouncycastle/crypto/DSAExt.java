/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.math.BigInteger;
import org.bouncycastle.crypto.DSA;

public interface DSAExt
extends DSA {
    public BigInteger getOrder();
}

