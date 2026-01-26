/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;

public interface MacCalculatorProvider {
    public MacCalculator get(AlgorithmIdentifier var1);
}

