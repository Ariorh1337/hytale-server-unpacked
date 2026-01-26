/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLPrivateKeyMethod;
import io.netty.handler.codec.quic.BoringSSLPrivateKeyMethodTask;
import java.util.function.BiConsumer;

final class BoringSSLPrivateKeyMethodDecryptTask
extends BoringSSLPrivateKeyMethodTask {
    private final byte[] input;

    BoringSSLPrivateKeyMethodDecryptTask(long ssl, byte[] input, BoringSSLPrivateKeyMethod method) {
        super(ssl, method);
        this.input = input;
    }

    @Override
    protected void runMethod(long ssl, BoringSSLPrivateKeyMethod method, BiConsumer<byte[], Throwable> consumer) {
        method.decrypt(ssl, this.input, consumer);
    }
}

