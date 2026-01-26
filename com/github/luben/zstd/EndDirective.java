/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

public enum EndDirective {
    CONTINUE(0),
    FLUSH(1),
    END(2);

    private final int value;

    private EndDirective(int n2) {
        this.value = n2;
    }

    int value() {
        return this.value;
    }
}

