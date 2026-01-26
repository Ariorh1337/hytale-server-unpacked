/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.util.Arrays;

public class MLKEMPrivateKeySpec
implements KeySpec {
    private final byte[] data;
    private final byte[] publicData;
    private final MLKEMParameterSpec params;
    private final boolean isSeed;

    public MLKEMPrivateKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] byArray) {
        if (byArray.length != 64) {
            throw new IllegalArgumentException("incorrect length for seed");
        }
        this.isSeed = true;
        this.params = mLKEMParameterSpec;
        this.data = Arrays.clone(byArray);
        this.publicData = null;
    }

    public MLKEMPrivateKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] byArray, byte[] byArray2) {
        this.isSeed = false;
        this.params = mLKEMParameterSpec;
        this.data = Arrays.clone(byArray);
        this.publicData = Arrays.clone(byArray2);
    }

    public boolean isSeed() {
        return this.isSeed;
    }

    public MLKEMParameterSpec getParameterSpec() {
        return this.params;
    }

    public byte[] getSeed() {
        if (this.isSeed()) {
            return Arrays.clone(this.data);
        }
        throw new IllegalStateException("KeySpec represents long form");
    }

    public byte[] getPrivateData() {
        if (!this.isSeed()) {
            return Arrays.clone(this.data);
        }
        throw new IllegalStateException("KeySpec represents seed");
    }

    public byte[] getPublicData() {
        if (!this.isSeed()) {
            return Arrays.clone(this.publicData);
        }
        throw new IllegalStateException("KeySpec represents long form");
    }
}

