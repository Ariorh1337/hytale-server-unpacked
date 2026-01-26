/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaders;

public interface HttpHeadersFactory {
    public HttpHeaders newHeaders();

    public HttpHeaders newEmptyHeaders();
}

