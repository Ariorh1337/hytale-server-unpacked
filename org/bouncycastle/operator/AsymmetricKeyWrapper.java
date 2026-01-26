/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.KeyWrapper;

public abstract class AsymmetricKeyWrapper
implements KeyWrapper {
    private AlgorithmIdentifier algorithmId;

    protected AsymmetricKeyWrapper(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithmId = algorithmIdentifier;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}

