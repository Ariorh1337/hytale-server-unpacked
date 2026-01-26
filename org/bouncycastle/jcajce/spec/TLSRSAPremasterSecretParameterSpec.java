/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class TLSRSAPremasterSecretParameterSpec
implements AlgorithmParameterSpec {
    private final int protocolVersion;

    public TLSRSAPremasterSecretParameterSpec(int n) {
        this.protocolVersion = n;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}

