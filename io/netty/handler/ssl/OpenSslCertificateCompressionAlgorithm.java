/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

public interface OpenSslCertificateCompressionAlgorithm {
    public byte[] compress(SSLEngine var1, byte[] var2) throws Exception;

    public byte[] decompress(SSLEngine var1, int var2, byte[] var3) throws Exception;

    public int algorithmId();
}

