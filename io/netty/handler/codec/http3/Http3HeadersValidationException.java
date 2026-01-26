/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

public final class Http3HeadersValidationException
extends RuntimeException {
    public Http3HeadersValidationException(String message) {
        super(message);
    }

    public Http3HeadersValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

