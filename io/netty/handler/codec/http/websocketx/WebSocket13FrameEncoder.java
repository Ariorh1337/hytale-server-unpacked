/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameMaskGenerator;

public class WebSocket13FrameEncoder
extends WebSocket08FrameEncoder {
    public WebSocket13FrameEncoder(boolean maskPayload) {
        super(maskPayload);
    }

    public WebSocket13FrameEncoder(WebSocketFrameMaskGenerator maskGenerator) {
        super(maskGenerator);
    }
}

