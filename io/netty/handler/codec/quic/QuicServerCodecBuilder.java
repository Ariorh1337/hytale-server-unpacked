/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.quic.FlushStrategy;
import io.netty.handler.codec.quic.NoQuicTokenHandler;
import io.netty.handler.codec.quic.Quic;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicCodecBuilder;
import io.netty.handler.codec.quic.QuicConnectionIdGenerator;
import io.netty.handler.codec.quic.QuicResetTokenGenerator;
import io.netty.handler.codec.quic.QuicSslEngine;
import io.netty.handler.codec.quic.QuicTokenHandler;
import io.netty.handler.codec.quic.QuicheConfig;
import io.netty.handler.codec.quic.QuicheQuicServerCodec;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public final class QuicServerCodecBuilder
extends QuicCodecBuilder<QuicServerCodecBuilder> {
    private final Map<ChannelOption<?>, Object> options = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> attrs = new HashMap();
    private final Map<ChannelOption<?>, Object> streamOptions = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> streamAttrs = new HashMap();
    private ChannelHandler handler;
    private ChannelHandler streamHandler;
    private QuicConnectionIdGenerator connectionIdAddressGenerator;
    private QuicTokenHandler tokenHandler;
    private QuicResetTokenGenerator resetTokenGenerator;

    public QuicServerCodecBuilder() {
        super(true);
    }

    private QuicServerCodecBuilder(QuicServerCodecBuilder builder) {
        super(builder);
        this.options.putAll(builder.options);
        this.attrs.putAll(builder.attrs);
        this.streamOptions.putAll(builder.streamOptions);
        this.streamAttrs.putAll(builder.streamAttrs);
        this.handler = builder.handler;
        this.streamHandler = builder.streamHandler;
        this.connectionIdAddressGenerator = builder.connectionIdAddressGenerator;
        this.tokenHandler = builder.tokenHandler;
        this.resetTokenGenerator = builder.resetTokenGenerator;
    }

    @Override
    public QuicServerCodecBuilder clone() {
        return new QuicServerCodecBuilder(this);
    }

    public <T> QuicServerCodecBuilder option(ChannelOption<T> option, @Nullable T value) {
        Quic.updateOptions(this.options, option, value);
        return (QuicServerCodecBuilder)this.self();
    }

    public <T> QuicServerCodecBuilder attr(AttributeKey<T> key, @Nullable T value) {
        Quic.updateAttributes(this.attrs, key, value);
        return (QuicServerCodecBuilder)this.self();
    }

    public QuicServerCodecBuilder handler(ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        return (QuicServerCodecBuilder)this.self();
    }

    public <T> QuicServerCodecBuilder streamOption(ChannelOption<T> option, @Nullable T value) {
        Quic.updateOptions(this.streamOptions, option, value);
        return (QuicServerCodecBuilder)this.self();
    }

    public <T> QuicServerCodecBuilder streamAttr(AttributeKey<T> key, @Nullable T value) {
        Quic.updateAttributes(this.streamAttrs, key, value);
        return (QuicServerCodecBuilder)this.self();
    }

    public QuicServerCodecBuilder streamHandler(ChannelHandler streamHandler) {
        this.streamHandler = ObjectUtil.checkNotNull(streamHandler, "streamHandler");
        return (QuicServerCodecBuilder)this.self();
    }

    public QuicServerCodecBuilder connectionIdAddressGenerator(QuicConnectionIdGenerator connectionIdAddressGenerator) {
        this.connectionIdAddressGenerator = connectionIdAddressGenerator;
        return this;
    }

    public QuicServerCodecBuilder tokenHandler(@Nullable QuicTokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
        return (QuicServerCodecBuilder)this.self();
    }

    public QuicServerCodecBuilder resetTokenGenerator(@Nullable QuicResetTokenGenerator resetTokenGenerator) {
        this.resetTokenGenerator = resetTokenGenerator;
        return (QuicServerCodecBuilder)this.self();
    }

    @Override
    protected void validate() {
        super.validate();
        if (this.handler == null && this.streamHandler == null) {
            throw new IllegalStateException("handler and streamHandler not set");
        }
    }

    @Override
    ChannelHandler build(QuicheConfig config, Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider, Executor sslTaskExecutor, int localConnIdLength, FlushStrategy flushStrategy) {
        QuicResetTokenGenerator resetTokenGenerator;
        QuicConnectionIdGenerator generator;
        this.validate();
        QuicTokenHandler tokenHandler = this.tokenHandler;
        if (tokenHandler == null) {
            tokenHandler = NoQuicTokenHandler.INSTANCE;
        }
        if ((generator = this.connectionIdAddressGenerator) == null) {
            generator = QuicConnectionIdGenerator.signGenerator();
        }
        if ((resetTokenGenerator = this.resetTokenGenerator) == null) {
            resetTokenGenerator = QuicResetTokenGenerator.signGenerator();
        }
        ChannelHandler handler = this.handler;
        ChannelHandler streamHandler = this.streamHandler;
        return new QuicheQuicServerCodec(config, localConnIdLength, tokenHandler, generator, resetTokenGenerator, flushStrategy, sslEngineProvider, sslTaskExecutor, handler, Quic.toOptionsArray(this.options), Quic.toAttributesArray(this.attrs), streamHandler, Quic.toOptionsArray(this.streamOptions), Quic.toAttributesArray(this.streamAttrs));
    }
}

