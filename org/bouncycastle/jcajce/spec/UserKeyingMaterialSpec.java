/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class UserKeyingMaterialSpec
implements AlgorithmParameterSpec {
    private final byte[] userKeyingMaterial;
    private final byte[] salt;

    public UserKeyingMaterialSpec(byte[] byArray) {
        this(byArray, null);
    }

    public UserKeyingMaterialSpec(byte[] byArray, byte[] byArray2) {
        this.userKeyingMaterial = Arrays.clone(byArray);
        this.salt = Arrays.clone(byArray2);
    }

    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
}

