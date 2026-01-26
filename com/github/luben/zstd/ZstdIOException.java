/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.Zstd;
import java.io.IOException;

public class ZstdIOException
extends IOException {
    private long code;

    public ZstdIOException(long l) {
        this(Zstd.getErrorCode(l), Zstd.getErrorName(l));
    }

    public ZstdIOException(long l, String string) {
        super(string);
        this.code = l;
    }

    public long getErrorCode() {
        return this.code;
    }
}

