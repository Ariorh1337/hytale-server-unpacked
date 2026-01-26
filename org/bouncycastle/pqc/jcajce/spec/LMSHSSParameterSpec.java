/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSParameterSpec;

public class LMSHSSParameterSpec
implements AlgorithmParameterSpec {
    private final LMSParameterSpec[] specs;

    public LMSHSSParameterSpec(LMSParameterSpec[] lMSParameterSpecArray) {
        this.specs = (LMSParameterSpec[])lMSParameterSpecArray.clone();
    }

    public LMSParameterSpec[] getLMSSpecs() {
        return (LMSParameterSpec[])this.specs.clone();
    }
}

