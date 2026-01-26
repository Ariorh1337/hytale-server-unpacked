/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class ParametersWithContext
implements CipherParameters {
    private CipherParameters parameters;
    private byte[] context;

    public ParametersWithContext(CipherParameters cipherParameters, byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.parameters = cipherParameters;
        this.context = Arrays.clone(byArray);
    }

    public void copyContextTo(byte[] byArray, int n, int n2) {
        if (this.context.length != n2) {
            throw new IllegalArgumentException("len");
        }
        System.arraycopy(this.context, 0, byArray, n, n2);
    }

    public byte[] getContext() {
        return Arrays.clone(this.context);
    }

    public int getContextLength() {
        return this.context.length;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}

