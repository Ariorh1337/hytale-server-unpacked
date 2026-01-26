/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeadersFactory;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeadersFactory;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpdyHttpDecoder
extends MessageToMessageDecoder<SpdyFrame> {
    private final int spdyVersion;
    private final int maxContentLength;
    private final Map<Integer, FullHttpMessage> messageMap;
    private final HttpHeadersFactory headersFactory;
    private final HttpHeadersFactory trailersFactory;

    public SpdyHttpDecoder(SpdyVersion version, int maxContentLength) {
        this(version, maxContentLength, new HashMap<Integer, FullHttpMessage>(), DefaultHttpHeadersFactory.headersFactory(), DefaultHttpHeadersFactory.trailersFactory());
    }

    @Deprecated
    public SpdyHttpDecoder(SpdyVersion version, int maxContentLength, boolean validateHeaders) {
        this(version, maxContentLength, new HashMap<Integer, FullHttpMessage>(), validateHeaders);
    }

    protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap) {
        this(version, maxContentLength, messageMap, DefaultHttpHeadersFactory.headersFactory(), DefaultHttpHeadersFactory.trailersFactory());
    }

    @Deprecated
    protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap, boolean validateHeaders) {
        this(version, maxContentLength, messageMap, DefaultHttpHeadersFactory.headersFactory().withValidation(validateHeaders), DefaultHttpHeadersFactory.trailersFactory().withValidation(validateHeaders));
    }

    protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap, HttpHeadersFactory headersFactory, HttpHeadersFactory trailersFactory) {
        super(SpdyFrame.class);
        this.spdyVersion = ObjectUtil.checkNotNull(version, "version").version();
        this.maxContentLength = ObjectUtil.checkPositive(maxContentLength, "maxContentLength");
        this.messageMap = messageMap;
        this.headersFactory = headersFactory;
        this.trailersFactory = trailersFactory;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        for (Map.Entry<Integer, FullHttpMessage> entry : this.messageMap.entrySet()) {
            ReferenceCountUtil.safeRelease(entry.getValue());
        }
        this.messageMap.clear();
        super.channelInactive(ctx);
    }

    protected FullHttpMessage putMessage(int streamId, FullHttpMessage message) {
        return this.messageMap.put(streamId, message);
    }

    protected FullHttpMessage getMessage(int streamId) {
        return this.messageMap.get(streamId);
    }

    protected FullHttpMessage removeMessage(int streamId) {
        return this.messageMap.remove(streamId);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, SpdyFrame msg, List<Object> out) throws Exception {
        block37: {
            if (msg instanceof SpdySynStreamFrame) {
                SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
                int streamId = spdySynStreamFrame.streamId();
                if (SpdyCodecUtil.isServerId(streamId)) {
                    int associatedToStreamId = spdySynStreamFrame.associatedStreamId();
                    if (associatedToStreamId == 0) {
                        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INVALID_STREAM);
                        ctx.writeAndFlush(spdyRstStreamFrame);
                        return;
                    }
                    if (spdySynStreamFrame.isLast()) {
                        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                        ctx.writeAndFlush(spdyRstStreamFrame);
                        return;
                    }
                    if (spdySynStreamFrame.isTruncated()) {
                        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                        ctx.writeAndFlush(spdyRstStreamFrame);
                        return;
                    }
                    try {
                        FullHttpRequest httpRequestWithEntity = SpdyHttpDecoder.createHttpRequest(spdySynStreamFrame, ctx.alloc());
                        httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
                        httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, associatedToStreamId);
                        httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.PRIORITY, spdySynStreamFrame.priority());
                        out.add(httpRequestWithEntity);
                    }
                    catch (Throwable ignored) {
                        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                        ctx.writeAndFlush(spdyRstStreamFrame);
                    }
                } else {
                    if (spdySynStreamFrame.isTruncated()) {
                        DefaultSpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                        spdySynReplyFrame.setLast(true);
                        SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
                        frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
                        frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, (Object)HttpVersion.HTTP_1_0);
                        ctx.writeAndFlush(spdySynReplyFrame);
                        return;
                    }
                    try {
                        FullHttpRequest httpRequestWithEntity = SpdyHttpDecoder.createHttpRequest(spdySynStreamFrame, ctx.alloc());
                        httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
                        if (spdySynStreamFrame.isLast()) {
                            out.add(httpRequestWithEntity);
                            break block37;
                        }
                        this.putMessage(streamId, httpRequestWithEntity);
                    }
                    catch (Throwable t) {
                        DefaultSpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                        spdySynReplyFrame.setLast(true);
                        SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
                        frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.BAD_REQUEST.code());
                        frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, (Object)HttpVersion.HTTP_1_0);
                        ctx.writeAndFlush(spdySynReplyFrame);
                    }
                }
            } else if (msg instanceof SpdySynReplyFrame) {
                SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
                int streamId = spdySynReplyFrame.streamId();
                if (spdySynReplyFrame.isTruncated()) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                    ctx.writeAndFlush(spdyRstStreamFrame);
                    return;
                }
                try {
                    FullHttpResponse httpResponseWithEntity = this.createHttpResponse(spdySynReplyFrame, ctx.alloc());
                    httpResponseWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
                    if (spdySynReplyFrame.isLast()) {
                        HttpUtil.setContentLength(httpResponseWithEntity, 0L);
                        out.add(httpResponseWithEntity);
                        break block37;
                    }
                    this.putMessage(streamId, httpResponseWithEntity);
                }
                catch (Throwable t) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                    ctx.writeAndFlush(spdyRstStreamFrame);
                }
            } else if (msg instanceof SpdyHeadersFrame) {
                SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
                int streamId = spdyHeadersFrame.streamId();
                FullHttpMessage fullHttpMessage = this.getMessage(streamId);
                if (fullHttpMessage == null) {
                    if (SpdyCodecUtil.isServerId(streamId)) {
                        if (spdyHeadersFrame.isTruncated()) {
                            DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                            ctx.writeAndFlush(spdyRstStreamFrame);
                            return;
                        }
                        try {
                            fullHttpMessage = this.createHttpResponse(spdyHeadersFrame, ctx.alloc());
                            fullHttpMessage.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
                            if (spdyHeadersFrame.isLast()) {
                                HttpUtil.setContentLength(fullHttpMessage, 0L);
                                out.add(fullHttpMessage);
                            } else {
                                this.putMessage(streamId, fullHttpMessage);
                            }
                        }
                        catch (Throwable t) {
                            DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                            ctx.writeAndFlush(spdyRstStreamFrame);
                        }
                    }
                    return;
                }
                if (!spdyHeadersFrame.isTruncated()) {
                    for (Map.Entry e : spdyHeadersFrame.headers()) {
                        fullHttpMessage.headers().add((CharSequence)e.getKey(), e.getValue());
                    }
                }
                if (spdyHeadersFrame.isLast()) {
                    HttpUtil.setContentLength(fullHttpMessage, fullHttpMessage.content().readableBytes());
                    this.removeMessage(streamId);
                    out.add(fullHttpMessage);
                }
            } else if (msg instanceof SpdyDataFrame) {
                SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
                int streamId = spdyDataFrame.streamId();
                FullHttpMessage fullHttpMessage = this.getMessage(streamId);
                if (fullHttpMessage == null) {
                    return;
                }
                ByteBuf content = fullHttpMessage.content();
                if (content.readableBytes() > this.maxContentLength - spdyDataFrame.content().readableBytes()) {
                    this.removeMessage(streamId);
                    throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes: " + spdyDataFrame.content().readableBytes());
                }
                ByteBuf spdyDataFrameData = spdyDataFrame.content();
                int spdyDataFrameDataLen = spdyDataFrameData.readableBytes();
                content.writeBytes(spdyDataFrameData, spdyDataFrameData.readerIndex(), spdyDataFrameDataLen);
                if (spdyDataFrame.isLast()) {
                    HttpUtil.setContentLength(fullHttpMessage, content.readableBytes());
                    this.removeMessage(streamId);
                    out.add(fullHttpMessage);
                }
            } else if (msg instanceof SpdyRstStreamFrame) {
                SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
                int streamId = spdyRstStreamFrame.streamId();
                this.removeMessage(streamId);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FullHttpRequest createHttpRequest(SpdyHeadersFrame requestFrame, ByteBufAllocator alloc) throws Exception {
        SpdyHeaders headers = requestFrame.headers();
        HttpMethod method = HttpMethod.valueOf(headers.getAsString(SpdyHeaders.HttpNames.METHOD));
        String url = headers.getAsString(SpdyHeaders.HttpNames.PATH);
        HttpVersion httpVersion = HttpVersion.valueOf(headers.getAsString(SpdyHeaders.HttpNames.VERSION));
        headers.remove(SpdyHeaders.HttpNames.METHOD);
        headers.remove(SpdyHeaders.HttpNames.PATH);
        headers.remove(SpdyHeaders.HttpNames.VERSION);
        boolean release = true;
        ByteBuf buffer = alloc.buffer();
        try {
            DefaultFullHttpRequest req = new DefaultFullHttpRequest(httpVersion, method, url, buffer);
            headers.remove(SpdyHeaders.HttpNames.SCHEME);
            CharSequence host = (CharSequence)headers.get(SpdyHeaders.HttpNames.HOST);
            headers.remove(SpdyHeaders.HttpNames.HOST);
            req.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)host);
            for (Map.Entry e : requestFrame.headers()) {
                req.headers().add((CharSequence)e.getKey(), e.getValue());
            }
            HttpUtil.setKeepAlive(req, true);
            req.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            release = false;
            DefaultFullHttpRequest defaultFullHttpRequest = req;
            return defaultFullHttpRequest;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private FullHttpResponse createHttpResponse(SpdyHeadersFrame responseFrame, ByteBufAllocator alloc) throws Exception {
        SpdyHeaders headers = responseFrame.headers();
        HttpResponseStatus status = HttpResponseStatus.parseLine((CharSequence)headers.get(SpdyHeaders.HttpNames.STATUS));
        HttpVersion version = HttpVersion.valueOf(headers.getAsString(SpdyHeaders.HttpNames.VERSION));
        headers.remove(SpdyHeaders.HttpNames.STATUS);
        headers.remove(SpdyHeaders.HttpNames.VERSION);
        boolean release = true;
        ByteBuf buffer = alloc.buffer();
        try {
            DefaultFullHttpResponse res = new DefaultFullHttpResponse(version, status, buffer, this.headersFactory, this.trailersFactory);
            for (Map.Entry e : responseFrame.headers()) {
                res.headers().add((CharSequence)e.getKey(), e.getValue());
            }
            HttpUtil.setKeepAlive(res, true);
            res.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            res.headers().remove(HttpHeaderNames.TRAILER);
            release = false;
            DefaultFullHttpResponse defaultFullHttpResponse = res;
            return defaultFullHttpResponse;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }
}

