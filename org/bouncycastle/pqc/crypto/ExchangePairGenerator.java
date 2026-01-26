/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;

public interface ExchangePairGenerator {
    public ExchangePair generateExchange(AsymmetricKeyParameter var1);
}

