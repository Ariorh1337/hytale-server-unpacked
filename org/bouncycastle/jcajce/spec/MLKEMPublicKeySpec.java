/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.util.Arrays;

public class MLKEMPublicKeySpec
implements KeySpec {
    private final MLKEMParameterSpec params;
    private final byte[] publicData;

    public MLKEMPublicKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] byArray) {
        this.params = mLKEMParameterSpec;
        this.publicData = Arrays.clone(byArray);
    }

    public MLKEMParameterSpec getParameterSpec() {
        return this.params;
    }

    public byte[] getPublicData() {
        return Arrays.clone(this.publicData);
    }
}

