/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalKey;

public interface ElGamalPrivateKey
extends ElGamalKey,
DHPrivateKey {
    @Override
    public BigInteger getX();
}

