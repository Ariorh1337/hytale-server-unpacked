/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import javax.crypto.SecretKey;
import org.bouncycastle.util.Arrays;

public final class SecretKeyWithEncapsulation
implements SecretKey {
    private final SecretKey secretKey;
    private final byte[] encapsulation;

    public SecretKeyWithEncapsulation(SecretKey secretKey, byte[] byArray) {
        this.secretKey = secretKey;
        this.encapsulation = Arrays.clone(byArray);
    }

    @Override
    public String getAlgorithm() {
        return this.secretKey.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return this.secretKey.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return this.secretKey.getEncoded();
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }

    public boolean equals(Object object) {
        return this.secretKey.equals(object);
    }

    public int hashCode() {
        return this.secretKey.hashCode();
    }
}

