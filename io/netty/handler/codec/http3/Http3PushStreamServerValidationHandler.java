/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3FrameTypeOutboundValidationHandler;
import io.netty.handler.codec.http3.Http3PushStreamFrame;

final class Http3PushStreamServerValidationHandler
extends Http3FrameTypeOutboundValidationHandler<Http3PushStreamFrame> {
    static final Http3PushStreamServerValidationHandler INSTANCE = new Http3PushStreamServerValidationHandler();

    private Http3PushStreamServerValidationHandler() {
        super(Http3PushStreamFrame.class);
    }

    @Override
    public boolean isSharable() {
        return true;
    }
}

