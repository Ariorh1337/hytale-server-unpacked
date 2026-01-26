/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.util.internal.ObjectUtil;
import org.jetbrains.annotations.Nullable;

public final class Http3Exception
extends Exception {
    private final Http3ErrorCode errorCode;

    public Http3Exception(Http3ErrorCode errorCode, @Nullable String message) {
        super(message);
        this.errorCode = ObjectUtil.checkNotNull(errorCode, "errorCode");
    }

    public Http3Exception(Http3ErrorCode errorCode, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.errorCode = ObjectUtil.checkNotNull(errorCode, "errorCode");
    }

    public Http3ErrorCode errorCode() {
        return this.errorCode;
    }
}

