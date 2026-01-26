/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.KeyUnwrapper;

public abstract class SymmetricKeyUnwrapper
implements KeyUnwrapper {
    private AlgorithmIdentifier algorithmId;

    protected SymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithmId = algorithmIdentifier;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}

