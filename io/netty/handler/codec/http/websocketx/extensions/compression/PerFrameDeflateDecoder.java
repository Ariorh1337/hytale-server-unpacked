/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder;

class PerFrameDeflateDecoder
extends DeflateDecoder {
    PerFrameDeflateDecoder(boolean noContext, int maxAllocation) {
        super(noContext, WebSocketExtensionFilter.NEVER_SKIP, maxAllocation);
    }

    PerFrameDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter, int maxAllocation) {
        super(noContext, extensionDecoderFilter, maxAllocation);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage(msg)) {
            return false;
        }
        WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (this.extensionDecoderFilter().mustSkip(wsFrame)) {
            return false;
        }
        return (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame || msg instanceof ContinuationWebSocketFrame) && (wsFrame.rsv() & 4) > 0;
    }

    @Override
    protected int newRsv(WebSocketFrame msg) {
        return msg.rsv() ^ 4;
    }

    @Override
    protected boolean appendFrameTail(WebSocketFrame msg) {
        return true;
    }
}

