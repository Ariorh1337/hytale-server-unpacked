/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameMaskGenerator;

public class WebSocket07FrameEncoder
extends WebSocket08FrameEncoder {
    public WebSocket07FrameEncoder(boolean maskPayload) {
        super(maskPayload);
    }

    public WebSocket07FrameEncoder(WebSocketFrameMaskGenerator maskGenerator) {
        super(maskGenerator);
    }
}

