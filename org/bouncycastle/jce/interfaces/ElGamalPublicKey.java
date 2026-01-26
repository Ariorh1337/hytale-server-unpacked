/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.jce.interfaces.ElGamalKey;

public interface ElGamalPublicKey
extends ElGamalKey,
DHPublicKey {
    @Override
    public BigInteger getY();
}

