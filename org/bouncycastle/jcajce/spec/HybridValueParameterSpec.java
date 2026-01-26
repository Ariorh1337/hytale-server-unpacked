/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.Destroyable;
import org.bouncycastle.util.Arrays;

public class HybridValueParameterSpec
implements AlgorithmParameterSpec,
Destroyable {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    private final boolean doPrepend;
    private volatile byte[] t;
    private volatile AlgorithmParameterSpec baseSpec;

    public HybridValueParameterSpec(byte[] byArray, AlgorithmParameterSpec algorithmParameterSpec) {
        this(byArray, false, algorithmParameterSpec);
    }

    public HybridValueParameterSpec(byte[] byArray, boolean bl, AlgorithmParameterSpec algorithmParameterSpec) {
        this.t = byArray;
        this.baseSpec = algorithmParameterSpec;
        this.doPrepend = bl;
    }

    public boolean isPrependedT() {
        return this.doPrepend;
    }

    public byte[] getT() {
        byte[] byArray = this.t;
        this.checkDestroyed();
        return byArray;
    }

    public AlgorithmParameterSpec getBaseParameterSpec() {
        AlgorithmParameterSpec algorithmParameterSpec = this.baseSpec;
        this.checkDestroyed();
        return algorithmParameterSpec;
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    @Override
    public void destroy() {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            Arrays.clear(this.t);
            this.t = null;
            this.baseSpec = null;
        }
    }

    private void checkDestroyed() {
        if (this.isDestroyed()) {
            throw new IllegalStateException("spec has been destroyed");
        }
    }
}

