/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.util.Arrays;

public class SecretWithEncapsulationImpl
implements SecretWithEncapsulation {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    private final byte[] sessionKey;
    private final byte[] cipher_text;

    public SecretWithEncapsulationImpl(byte[] byArray, byte[] byArray2) {
        this.sessionKey = byArray;
        this.cipher_text = byArray2;
    }

    @Override
    public byte[] getSecret() {
        byte[] byArray = Arrays.clone(this.sessionKey);
        this.checkDestroyed();
        return byArray;
    }

    @Override
    public byte[] getEncapsulation() {
        byte[] byArray = Arrays.clone(this.cipher_text);
        this.checkDestroyed();
        return byArray;
    }

    @Override
    public void destroy() throws DestroyFailedException {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            Arrays.clear(this.sessionKey);
            Arrays.clear(this.cipher_text);
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    void checkDestroyed() {
        if (this.isDestroyed()) {
            throw new IllegalStateException("data has been destroyed");
        }
    }
}

