/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.Zuc256CoreEngine;
import org.bouncycastle.util.Memoable;

public final class Zuc256Engine
extends Zuc256CoreEngine {
    public Zuc256Engine() {
    }

    public Zuc256Engine(int n) {
        super(n);
    }

    private Zuc256Engine(Zuc256Engine zuc256Engine) {
        super(zuc256Engine);
    }

    @Override
    public Memoable copy() {
        return new Zuc256Engine(this);
    }
}

