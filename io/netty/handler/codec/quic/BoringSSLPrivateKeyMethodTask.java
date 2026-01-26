/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLPrivateKeyMethod;
import io.netty.handler.codec.quic.BoringSSLTask;
import java.util.function.BiConsumer;

abstract class BoringSSLPrivateKeyMethodTask
extends BoringSSLTask {
    private final BoringSSLPrivateKeyMethod method;
    private byte[] resultBytes;

    BoringSSLPrivateKeyMethodTask(long ssl, BoringSSLPrivateKeyMethod method) {
        super(ssl);
        this.method = method;
    }

    @Override
    protected final void runTask(long ssl, BoringSSLTask.TaskCallback callback) {
        this.runMethod(ssl, this.method, (result, error) -> {
            if (result == null || error != null) {
                callback.onResult(ssl, -1);
            } else {
                this.resultBytes = result;
                callback.onResult(ssl, 1);
            }
        });
    }

    protected abstract void runMethod(long var1, BoringSSLPrivateKeyMethod var3, BiConsumer<byte[], Throwable> var4);
}

