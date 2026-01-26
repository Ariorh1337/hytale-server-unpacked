/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class ContextParameterSpec
implements AlgorithmParameterSpec {
    public static ContextParameterSpec EMPTY_CONTEXT_SPEC = new ContextParameterSpec(new byte[0]);
    private final byte[] context;

    public ContextParameterSpec(byte[] byArray) {
        this.context = Arrays.clone(byArray);
    }

    public byte[] getContext() {
        return Arrays.clone(this.context);
    }
}

