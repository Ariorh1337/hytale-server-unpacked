/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.quic.QuicException;
import io.netty.handler.codec.quic.QuicPacketType;
import io.netty.handler.codec.quic.QuicTransportError;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public final class QuicHeaderParser
implements AutoCloseable {
    private static final int AES_128_GCM_TAG_LENGTH = 16;
    private final int localConnectionIdLength;
    private boolean closed;

    public QuicHeaderParser(int localConnectionIdLength) {
        this.localConnectionIdLength = ObjectUtil.checkPositiveOrZero(localConnectionIdLength, "localConnectionIdLength");
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
        }
    }

    public void parse(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf packet, QuicHeaderProcessor callback) throws Exception {
        ByteBuf dcid;
        ByteBuf token;
        ByteBuf scid;
        QuicPacketType type;
        long version;
        if (this.closed) {
            throw new IllegalStateException(QuicHeaderParser.class.getSimpleName() + " is already closed");
        }
        int offset = 0;
        int readable = packet.readableBytes();
        QuicHeaderParser.checkReadable(offset, readable, 1);
        byte first = packet.getByte(offset);
        ++offset;
        if (QuicHeaderParser.hasShortHeader(first)) {
            version = 0L;
            type = QuicPacketType.SHORT;
            scid = Unpooled.EMPTY_BUFFER;
            token = Unpooled.EMPTY_BUFFER;
            dcid = QuicHeaderParser.sliceCid(packet, offset, this.localConnectionIdLength);
        } else {
            QuicHeaderParser.checkReadable(offset, readable, 4);
            version = packet.getUnsignedInt(offset);
            type = QuicHeaderParser.typeOfLongHeader(first, version);
            short dcidLen = packet.getUnsignedByte(offset += 4);
            QuicHeaderParser.checkCidLength(dcidLen);
            dcid = QuicHeaderParser.sliceCid(packet, ++offset, dcidLen);
            short scidLen = packet.getUnsignedByte(offset += dcidLen);
            QuicHeaderParser.checkCidLength(scidLen);
            scid = QuicHeaderParser.sliceCid(packet, ++offset, scidLen);
            token = QuicHeaderParser.sliceToken(type, packet, offset += scidLen, readable);
        }
        callback.process(sender, recipient, packet, type, version, scid, dcid, token);
    }

    private static void checkCidLength(int length) throws QuicException {
        if (length > 20) {
            throw new QuicException("connection id to large: " + length + " > " + 20, QuicTransportError.PROTOCOL_VIOLATION);
        }
    }

    private static ByteBuf sliceToken(QuicPacketType type, ByteBuf packet, int offset, int readable) throws QuicException {
        switch (type) {
            case INITIAL: {
                QuicHeaderParser.checkReadable(offset, readable, 1);
                int numBytes = QuicHeaderParser.numBytesForVariableLengthInteger(packet.getByte(offset));
                int len = (int)QuicHeaderParser.getVariableLengthInteger(packet, offset, numBytes);
                QuicHeaderParser.checkReadable(offset += numBytes, readable, len);
                return packet.slice(offset, len);
            }
            case RETRY: {
                QuicHeaderParser.checkReadable(offset, readable, 16);
                int tokenLen = readable - offset - 16;
                return packet.slice(offset, tokenLen);
            }
        }
        return Unpooled.EMPTY_BUFFER;
    }

    private static QuicException newProtocolViolationException(String message) {
        return new QuicException(message, QuicTransportError.PROTOCOL_VIOLATION);
    }

    static ByteBuf sliceCid(ByteBuf buffer, int offset, int len) throws QuicException {
        QuicHeaderParser.checkReadable(offset, buffer.readableBytes(), len);
        return buffer.slice(offset, len);
    }

    private static void checkReadable(int offset, int readable, int needed) throws QuicException {
        int r = readable - offset;
        if (r < needed) {
            throw QuicHeaderParser.newProtocolViolationException("Not enough bytes to read, " + r + " < " + needed);
        }
    }

    private static long getVariableLengthInteger(ByteBuf in, int offset, int len) throws QuicException {
        QuicHeaderParser.checkReadable(offset, in.readableBytes(), len);
        switch (len) {
            case 1: {
                return in.getUnsignedByte(offset);
            }
            case 2: {
                return in.getUnsignedShort(offset) & 0x3FFF;
            }
            case 4: {
                return in.getUnsignedInt(offset) & 0x3FFFFFFFL;
            }
            case 8: {
                return in.getLong(offset) & 0x3FFFFFFFFFFFFFFFL;
            }
        }
        throw QuicHeaderParser.newProtocolViolationException("Unsupported length:" + len);
    }

    private static int numBytesForVariableLengthInteger(byte b) {
        byte val = (byte)(b >> 6);
        if ((val & 1) != 0) {
            if ((val & 2) != 0) {
                return 8;
            }
            return 2;
        }
        if ((val & 2) != 0) {
            return 4;
        }
        return 1;
    }

    static boolean hasShortHeader(byte b) {
        return (b & 0x80) == 0;
    }

    private static QuicPacketType typeOfLongHeader(byte first, long version) throws QuicException {
        if (version == 0L) {
            return QuicPacketType.VERSION_NEGOTIATION;
        }
        int packetType = (first & 0x30) >> 4;
        switch (packetType) {
            case 0: {
                return QuicPacketType.INITIAL;
            }
            case 1: {
                return QuicPacketType.ZERO_RTT;
            }
            case 2: {
                return QuicPacketType.HANDSHAKE;
            }
            case 3: {
                return QuicPacketType.RETRY;
            }
        }
        throw QuicHeaderParser.newProtocolViolationException("Unknown packet type: " + packetType);
    }

    public static interface QuicHeaderProcessor {
        public void process(InetSocketAddress var1, InetSocketAddress var2, ByteBuf var3, QuicPacketType var4, long var5, ByteBuf var7, ByteBuf var8, ByteBuf var9) throws Exception;
    }
}

