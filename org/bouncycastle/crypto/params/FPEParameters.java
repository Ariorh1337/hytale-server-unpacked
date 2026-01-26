/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;

public final class FPEParameters
implements CipherParameters {
    private final KeyParameter key;
    private final RadixConverter radixConverter;
    private final byte[] tweak;
    private final boolean useInverse;

    public FPEParameters(KeyParameter keyParameter, int n, byte[] byArray) {
        this(keyParameter, n, byArray, false);
    }

    public FPEParameters(KeyParameter keyParameter, int n, byte[] byArray, boolean bl) {
        this(keyParameter, new RadixConverter(n), byArray, bl);
    }

    public FPEParameters(KeyParameter keyParameter, RadixConverter radixConverter, byte[] byArray, boolean bl) {
        this.key = keyParameter;
        this.radixConverter = radixConverter;
        this.tweak = Arrays.clone(byArray);
        this.useInverse = bl;
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public int getRadix() {
        return this.radixConverter.getRadix();
    }

    public RadixConverter getRadixConverter() {
        return this.radixConverter;
    }

    public byte[] getTweak() {
        return Arrays.clone(this.tweak);
    }

    public boolean isUsingInverseFunction() {
        return this.useInverse;
    }
}

