/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.Zstd;

public class ZstdException
extends RuntimeException {
    private long code;

    public ZstdException(long l) {
        this(Zstd.getErrorCode(l), Zstd.getErrorName(l));
    }

    public ZstdException(long l, String string) {
        super(string);
        this.code = l;
    }

    public long getErrorCode() {
        return this.code;
    }
}

