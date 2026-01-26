/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.crypto.threshold.SecretShare;
import org.bouncycastle.util.Arrays;

public class ShamirSplitSecretShare
implements SecretShare {
    private final byte[] secretShare;
    final int r;

    public ShamirSplitSecretShare(byte[] byArray, int n) {
        this.secretShare = Arrays.clone(byArray);
        this.r = n;
    }

    public ShamirSplitSecretShare(byte[] byArray) {
        this.secretShare = Arrays.clone(byArray);
        this.r = 1;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return Arrays.clone(this.secretShare);
    }
}

