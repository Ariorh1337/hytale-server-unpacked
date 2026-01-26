/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.AutoCloseBase;

abstract class SharedDictBase
extends AutoCloseBase {
    SharedDictBase() {
    }

    protected void finalize() {
        this.close();
    }
}

