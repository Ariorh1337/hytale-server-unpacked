/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslSession;
import io.netty.handler.ssl.OpenSslSessionId;
import java.security.cert.Certificate;
import java.util.Map;
import javax.net.ssl.SSLException;

interface OpenSslInternalSession
extends OpenSslSession {
    public void prepareHandshake();

    public OpenSslSessionId sessionId();

    public void setLocalCertificate(Certificate[] var1);

    public void setSessionDetails(long var1, long var3, OpenSslSessionId var5, Map<String, Object> var6);

    public Map<String, Object> keyValueStorage();

    public void setLastAccessedTime(long var1);

    public void tryExpandApplicationBufferSize(int var1);

    public void handshakeFinished(byte[] var1, String var2, String var3, byte[] var4, byte[][] var5, long var6, long var8) throws SSLException;
}

