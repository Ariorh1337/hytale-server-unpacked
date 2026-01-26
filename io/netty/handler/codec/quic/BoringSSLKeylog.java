/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import javax.net.ssl.SSLEngine;

public interface BoringSSLKeylog {
    public void logKey(SSLEngine var1, String var2);
}

