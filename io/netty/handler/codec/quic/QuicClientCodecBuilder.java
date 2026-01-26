/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.quic.FlushStrategy;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicCodecBuilder;
import io.netty.handler.codec.quic.QuicSslEngine;
import io.netty.handler.codec.quic.QuicheConfig;
import io.netty.handler.codec.quic.QuicheQuicClientCodec;
import java.util.concurrent.Executor;
import java.util.function.Function;

public final class QuicClientCodecBuilder
extends QuicCodecBuilder<QuicClientCodecBuilder> {
    public QuicClientCodecBuilder() {
        super(false);
    }

    private QuicClientCodecBuilder(QuicCodecBuilder<QuicClientCodecBuilder> builder) {
        super(builder);
    }

    @Override
    public QuicClientCodecBuilder clone() {
        return new QuicClientCodecBuilder(this);
    }

    @Override
    ChannelHandler build(QuicheConfig config, Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider, Executor sslTaskExecutor, int localConnIdLength, FlushStrategy flushStrategy) {
        return new QuicheQuicClientCodec(config, sslEngineProvider, sslTaskExecutor, localConnIdLength, flushStrategy);
    }
}

