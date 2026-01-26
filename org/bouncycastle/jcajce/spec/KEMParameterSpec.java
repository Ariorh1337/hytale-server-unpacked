/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import org.bouncycastle.jcajce.spec.KTSParameterSpec;

public class KEMParameterSpec
extends KTSParameterSpec {
    public KEMParameterSpec(String string) {
        this(string, 256);
    }

    public KEMParameterSpec(String string, int n) {
        super(string, n, null, null, null);
    }

    public int getKeySizeInBits() {
        return this.getKeySize();
    }
}

