/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslSessionContext;
import javax.net.ssl.SSLSession;

public interface OpenSslSession
extends SSLSession {
    public boolean hasPeerCertificates();

    @Override
    public OpenSslSessionContext getSessionContext();
}

