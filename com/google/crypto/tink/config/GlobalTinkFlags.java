/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.config;

import com.google.crypto.tink.config.TinkFlag;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GlobalTinkFlags {
    public static final TinkFlag validateKeysetsOnParsing = new TinkFlagImpl(false);

    private GlobalTinkFlags() {
    }

    private static class TinkFlagImpl
    implements TinkFlag {
        private final AtomicBoolean b;

        TinkFlagImpl(boolean b) {
            this.b = new AtomicBoolean(b);
        }

        @Override
        public boolean getValue() {
            return this.b.get();
        }

        @Override
        public void setValue(boolean t) {
            this.b.set(t);
        }
    }
}

