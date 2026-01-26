/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;

public class WebSocketServerCompressionHandler
extends WebSocketServerExtensionHandler {
    @Deprecated
    public WebSocketServerCompressionHandler() {
        this(0);
    }

    public WebSocketServerCompressionHandler(int maxAllocation) {
        super(new PerMessageDeflateServerExtensionHandshaker(maxAllocation), new DeflateFrameServerExtensionHandshaker(6, maxAllocation));
    }
}

