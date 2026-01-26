/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http3.Http3CodecUtils;
import io.netty.handler.codec.http3.Http3DataFrame;
import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3Exception;
import io.netty.handler.codec.http3.Http3FrameValidationUtils;
import io.netty.handler.codec.http3.Http3HeadersFrame;
import io.netty.handler.codec.http3.Http3PushPromiseFrame;
import io.netty.handler.codec.http3.Http3RequestStreamCodecState;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;
import io.netty.handler.codec.http3.QpackAttributes;
import io.netty.handler.codec.http3.QpackDecoder;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import java.util.function.BooleanSupplier;

final class Http3RequestStreamValidationUtils {
    static final long CONTENT_LENGTH_NOT_MODIFIED = -1L;
    static final long INVALID_FRAME_READ = -2L;

    private Http3RequestStreamValidationUtils() {
    }

    static boolean validateClientWrite(Http3RequestStreamFrame frame, ChannelPromise promise, ChannelHandlerContext ctx, BooleanSupplier goAwayReceivedSupplier, Http3RequestStreamCodecState encodeState) {
        if (goAwayReceivedSupplier.getAsBoolean() && !encodeState.started()) {
            String type = StringUtil.simpleClassName(frame);
            ReferenceCountUtil.release(frame);
            promise.setFailure(new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Frame of type " + type + " unexpected as we received a GOAWAY already."));
            ctx.close();
            return false;
        }
        if (frame instanceof Http3PushPromiseFrame) {
            Http3FrameValidationUtils.frameTypeUnexpected(promise, (Object)frame);
            return false;
        }
        return true;
    }

    static long validateHeaderFrameRead(Http3HeadersFrame headersFrame, ChannelHandlerContext ctx, Http3RequestStreamCodecState decodeState) {
        if (headersFrame.headers().contains(HttpHeaderNames.CONNECTION)) {
            Http3RequestStreamValidationUtils.headerUnexpected(ctx, headersFrame, "connection header included");
            return -2L;
        }
        CharSequence value = (CharSequence)headersFrame.headers().get(HttpHeaderNames.TE);
        if (value != null && !HttpHeaderValues.TRAILERS.equals(value)) {
            Http3RequestStreamValidationUtils.headerUnexpected(ctx, headersFrame, "te header field included with invalid value: " + value);
            return -2L;
        }
        if (decodeState.receivedFinalHeaders()) {
            long length = HttpUtil.normalizeAndGetContentLength(headersFrame.headers().getAll(HttpHeaderNames.CONTENT_LENGTH), false, true);
            if (length != -1L) {
                headersFrame.headers().setLong(HttpHeaderNames.CONTENT_LENGTH, length);
            }
            return length;
        }
        return -1L;
    }

    static long validateDataFrameRead(Http3DataFrame dataFrame, ChannelHandlerContext ctx, long expectedLength, long seenLength, boolean clientHeadRequest) {
        try {
            return Http3RequestStreamValidationUtils.verifyContentLength(dataFrame.content().readableBytes(), expectedLength, seenLength, false, clientHeadRequest);
        }
        catch (Http3Exception e) {
            ReferenceCountUtil.release(dataFrame);
            Http3RequestStreamValidationUtils.failStream(ctx, e);
            return -2L;
        }
    }

    static boolean validateOnStreamClosure(ChannelHandlerContext ctx, long expectedLength, long seenLength, boolean clientHeadRequest) {
        try {
            Http3RequestStreamValidationUtils.verifyContentLength(0, expectedLength, seenLength, true, clientHeadRequest);
            return true;
        }
        catch (Http3Exception e) {
            ctx.fireExceptionCaught(e);
            Http3CodecUtils.streamError(ctx, e.errorCode());
            return false;
        }
    }

    static void sendStreamAbandonedIfRequired(ChannelHandlerContext ctx, QpackAttributes qpackAttributes, QpackDecoder qpackDecoder, Http3RequestStreamCodecState decodeState) {
        if (!qpackAttributes.dynamicTableDisabled() && !decodeState.terminated()) {
            long streamId = ((QuicStreamChannel)ctx.channel()).streamId();
            if (qpackAttributes.decoderStreamAvailable()) {
                qpackDecoder.streamAbandoned(qpackAttributes.decoderStream(), streamId);
            } else {
                qpackAttributes.whenDecoderStreamAvailable(future -> {
                    if (future.isSuccess()) {
                        qpackDecoder.streamAbandoned(qpackAttributes.decoderStream(), streamId);
                    }
                });
            }
        }
    }

    private static void headerUnexpected(ChannelHandlerContext ctx, Http3RequestStreamFrame frame, String msg) {
        ReferenceCountUtil.release(frame);
        Http3RequestStreamValidationUtils.failStream(ctx, new Http3Exception(Http3ErrorCode.H3_MESSAGE_ERROR, msg));
    }

    private static void failStream(ChannelHandlerContext ctx, Http3Exception cause) {
        ctx.fireExceptionCaught(cause);
        Http3CodecUtils.streamError(ctx, cause.errorCode());
    }

    private static long verifyContentLength(int length, long expectedLength, long seenLength, boolean end, boolean clientHeadRequest) throws Http3Exception {
        if (expectedLength != -1L && ((seenLength += (long)length) > expectedLength || !clientHeadRequest && end && seenLength != expectedLength)) {
            throw new Http3Exception(Http3ErrorCode.H3_MESSAGE_ERROR, "Expected content-length " + expectedLength + " != " + seenLength + ".");
        }
        return seenLength;
    }
}

