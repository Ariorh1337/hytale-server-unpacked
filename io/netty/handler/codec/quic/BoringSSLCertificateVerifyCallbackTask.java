/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLCertificateVerifyCallback;
import io.netty.handler.codec.quic.BoringSSLTask;

final class BoringSSLCertificateVerifyCallbackTask
extends BoringSSLTask {
    private final byte[][] x509;
    private final String authAlgorithm;
    private final BoringSSLCertificateVerifyCallback verifier;

    BoringSSLCertificateVerifyCallbackTask(long ssl, byte[][] x509, String authAlgorithm, BoringSSLCertificateVerifyCallback verifier) {
        super(ssl);
        this.x509 = x509;
        this.authAlgorithm = authAlgorithm;
        this.verifier = verifier;
    }

    @Override
    protected void runTask(long ssl, BoringSSLTask.TaskCallback callback) {
        int result = this.verifier.verify(ssl, this.x509, this.authAlgorithm);
        callback.onResult(ssl, result);
    }
}

