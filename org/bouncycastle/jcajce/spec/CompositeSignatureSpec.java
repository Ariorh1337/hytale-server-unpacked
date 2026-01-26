/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class CompositeSignatureSpec
implements AlgorithmParameterSpec {
    private final boolean isPrehashMode;
    private final AlgorithmParameterSpec secondaryParameterSpec;

    public CompositeSignatureSpec(boolean bl) {
        this(bl, null);
    }

    public CompositeSignatureSpec(boolean bl, AlgorithmParameterSpec algorithmParameterSpec) {
        this.isPrehashMode = bl;
        this.secondaryParameterSpec = algorithmParameterSpec;
    }

    public boolean isPrehashMode() {
        return this.isPrehashMode;
    }

    public AlgorithmParameterSpec getSecondarySpec() {
        return this.secondaryParameterSpec;
    }
}

