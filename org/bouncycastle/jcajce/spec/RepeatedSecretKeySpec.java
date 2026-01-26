/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import javax.crypto.SecretKey;

public class RepeatedSecretKeySpec
implements SecretKey {
    private String algorithm;

    public RepeatedSecretKeySpec(String string) {
        this.algorithm = string;
    }

    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return null;
    }
}

