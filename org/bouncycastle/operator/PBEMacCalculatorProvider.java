/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;

public interface PBEMacCalculatorProvider {
    public MacCalculator get(AlgorithmIdentifier var1, char[] var2) throws OperatorCreationException;
}

