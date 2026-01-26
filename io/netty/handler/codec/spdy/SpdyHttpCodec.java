/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeadersFactory;
import io.netty.handler.codec.spdy.SpdyHttpDecoder;
import io.netty.handler.codec.spdy.SpdyHttpEncoder;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.util.HashMap;

public final class SpdyHttpCodec
extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder> {
    public SpdyHttpCodec(SpdyVersion version, int maxContentLength) {
        super(new SpdyHttpDecoder(version, maxContentLength), new SpdyHttpEncoder(version));
    }

    @Deprecated
    public SpdyHttpCodec(SpdyVersion version, int maxContentLength, boolean validateHttpHeaders) {
        super(new SpdyHttpDecoder(version, maxContentLength, validateHttpHeaders), new SpdyHttpEncoder(version));
    }

    public SpdyHttpCodec(SpdyVersion version, int maxContentLength, HttpHeadersFactory headersFactory, HttpHeadersFactory trailersFactory) {
        super(new SpdyHttpDecoder(version, maxContentLength, new HashMap<Integer, FullHttpMessage>(), headersFactory, trailersFactory), new SpdyHttpEncoder(version));
    }
}

