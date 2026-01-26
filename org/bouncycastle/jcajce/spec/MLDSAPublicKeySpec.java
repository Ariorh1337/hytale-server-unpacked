/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.util.Arrays;

public class MLDSAPublicKeySpec
implements KeySpec {
    private final MLDSAParameterSpec params;
    private final byte[] publicData;

    public MLDSAPublicKeySpec(MLDSAParameterSpec mLDSAParameterSpec, byte[] byArray) {
        this.params = mLDSAParameterSpec;
        this.publicData = Arrays.clone(byArray);
    }

    public MLDSAParameterSpec getParameterSpec() {
        return this.params;
    }

    public byte[] getPublicData() {
        return Arrays.clone(this.publicData);
    }
}

