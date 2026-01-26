/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.Zuc128CoreEngine;
import org.bouncycastle.util.Memoable;

public final class Zuc128Engine
extends Zuc128CoreEngine {
    public Zuc128Engine() {
    }

    private Zuc128Engine(Zuc128Engine zuc128Engine) {
        super(zuc128Engine);
    }

    @Override
    public Memoable copy() {
        return new Zuc128Engine(this);
    }
}

