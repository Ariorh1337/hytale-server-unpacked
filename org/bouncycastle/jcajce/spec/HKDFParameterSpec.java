/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import org.bouncycastle.crypto.params.HKDFParameters;

public class HKDFParameterSpec
implements KeySpec,
AlgorithmParameterSpec {
    private final HKDFParameters hkdfParameters;
    private final int outputLength;

    public HKDFParameterSpec(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        this.hkdfParameters = new HKDFParameters(byArray, byArray2, byArray3);
        this.outputLength = n;
    }

    public byte[] getIKM() {
        return this.hkdfParameters.getIKM();
    }

    public boolean skipExtract() {
        return this.hkdfParameters.skipExtract();
    }

    public byte[] getSalt() {
        return this.hkdfParameters.getSalt();
    }

    public byte[] getInfo() {
        return this.hkdfParameters.getInfo();
    }

    public int getOutputLength() {
        return this.outputLength;
    }
}

