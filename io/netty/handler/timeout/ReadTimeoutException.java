/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.timeout;

import io.netty.handler.timeout.TimeoutException;

public final class ReadTimeoutException
extends TimeoutException {
    private static final long serialVersionUID = 169287984113283421L;
    public static final ReadTimeoutException INSTANCE = new ReadTimeoutException(true);

    public ReadTimeoutException() {
    }

    public ReadTimeoutException(String message) {
        super(message, false);
    }

    private ReadTimeoutException(boolean shared) {
        super(null, shared);
    }
}

