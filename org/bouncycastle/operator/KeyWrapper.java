/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public interface KeyWrapper {
    public AlgorithmIdentifier getAlgorithmIdentifier();

    public byte[] generateWrappedKey(GenericKey var1) throws OperatorException;
}

