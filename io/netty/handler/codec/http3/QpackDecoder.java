/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http3.Http3CodecUtils;
import io.netty.handler.codec.http3.QpackAttributes;
import io.netty.handler.codec.http3.QpackDecoderDynamicTable;
import io.netty.handler.codec.http3.QpackDecoderStateSyncStrategy;
import io.netty.handler.codec.http3.QpackException;
import io.netty.handler.codec.http3.QpackHeaderField;
import io.netty.handler.codec.http3.QpackHuffmanDecoder;
import io.netty.handler.codec.http3.QpackStaticTable;
import io.netty.handler.codec.http3.QpackUtil;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.AsciiString;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class QpackDecoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(QpackDecoder.class);
    private static final QpackException DYNAMIC_TABLE_CAPACITY_EXCEEDS_MAX = QpackException.newStatic(QpackDecoder.class, "setDynamicTableCapacity(...)", "QPACK - decoder dynamic table capacity exceeds max capacity.");
    private static final QpackException HEADER_ILLEGAL_INDEX_VALUE = QpackException.newStatic(QpackDecoder.class, "decodeIndexed(...)", "QPACK - illegal index value");
    private static final QpackException NAME_ILLEGAL_INDEX_VALUE = QpackException.newStatic(QpackDecoder.class, "decodeLiteralWithNameRef(...)", "QPACK - illegal name index value");
    private static final QpackException INVALID_REQUIRED_INSERT_COUNT = QpackException.newStatic(QpackDecoder.class, "decodeRequiredInsertCount(...)", "QPACK - invalid required insert count");
    private static final QpackException MAX_BLOCKED_STREAMS_EXCEEDED = QpackException.newStatic(QpackDecoder.class, "shouldWaitForDynamicTableUpdates(...)", "QPACK - exceeded max blocked streams");
    private static final QpackException BLOCKED_STREAM_RESUMPTION_FAILED = QpackException.newStatic(QpackDecoder.class, "sendInsertCountIncrementIfRequired(...)", "QPACK - failed to resume a blocked stream");
    private static final QpackException UNKNOWN_TYPE = QpackException.newStatic(QpackDecoder.class, "decode(...)", "QPACK - unknown type");
    private final QpackHuffmanDecoder huffmanDecoder = new QpackHuffmanDecoder();
    private final QpackDecoderDynamicTable dynamicTable;
    private final long maxTableCapacity;
    private final int maxBlockedStreams;
    private final QpackDecoderStateSyncStrategy stateSyncStrategy;
    private final IntObjectHashMap<List<Runnable>> blockedStreams;
    private final long maxEntries;
    private final long fullRange;
    private int blockedStreamsCount;
    private long lastAckInsertCount;

    QpackDecoder(long maxTableCapacity, int maxBlockedStreams) {
        this(maxTableCapacity, maxBlockedStreams, new QpackDecoderDynamicTable(), QpackDecoderStateSyncStrategy.ackEachInsert());
    }

    QpackDecoder(long maxTableCapacity, int maxBlockedStreams, QpackDecoderDynamicTable dynamicTable, QpackDecoderStateSyncStrategy stateSyncStrategy) {
        this.maxTableCapacity = maxTableCapacity;
        this.maxBlockedStreams = maxBlockedStreams;
        this.stateSyncStrategy = stateSyncStrategy;
        this.blockedStreams = new IntObjectHashMap(Math.min(16, maxBlockedStreams));
        this.dynamicTable = dynamicTable;
        this.maxEntries = QpackUtil.maxEntries(maxTableCapacity);
        try {
            this.fullRange = QpackUtil.toIntOrThrow(2L * this.maxEntries);
        }
        catch (QpackException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean decode(QpackAttributes qpackAttributes, long streamId, ByteBuf in, int length, BiConsumer<CharSequence, CharSequence> sink, Runnable whenDecoded) throws QpackException {
        int initialReaderIdx = in.readerIndex();
        int requiredInsertCount = this.decodeRequiredInsertCount(qpackAttributes, in);
        if (this.shouldWaitForDynamicTableUpdates(requiredInsertCount)) {
            ++this.blockedStreamsCount;
            this.blockedStreams.computeIfAbsent(requiredInsertCount, __ -> new ArrayList(2)).add(whenDecoded);
            in.readerIndex(initialReaderIdx);
            return false;
        }
        in = in.readSlice(length - (in.readerIndex() - initialReaderIdx));
        int base = this.decodeBase(in, requiredInsertCount);
        while (in.isReadable()) {
            byte b = in.getByte(in.readerIndex());
            if (QpackDecoder.isIndexed(b)) {
                this.decodeIndexed(in, sink, base);
                continue;
            }
            if (QpackDecoder.isIndexedWithPostBase(b)) {
                this.decodeIndexedWithPostBase(in, sink, base);
                continue;
            }
            if (QpackDecoder.isLiteralWithNameRef(b)) {
                this.decodeLiteralWithNameRef(in, sink, base);
                continue;
            }
            if (QpackDecoder.isLiteralWithPostBaseNameRef(b)) {
                this.decodeLiteralWithPostBaseNameRef(in, sink, base);
                continue;
            }
            if (QpackDecoder.isLiteral(b)) {
                this.decodeLiteral(in, sink);
                continue;
            }
            throw UNKNOWN_TYPE;
        }
        if (requiredInsertCount > 0) {
            assert (!qpackAttributes.dynamicTableDisabled());
            assert (qpackAttributes.decoderStreamAvailable());
            this.stateSyncStrategy.sectionAcknowledged(requiredInsertCount);
            ByteBuf sectionAck = qpackAttributes.decoderStream().alloc().buffer(8);
            QpackUtil.encodePrefixedInteger(sectionAck, (byte)-128, 7, streamId);
            Http3CodecUtils.closeOnFailure(qpackAttributes.decoderStream().writeAndFlush(sectionAck));
        }
        return true;
    }

    void setDynamicTableCapacity(long capacity) throws QpackException {
        if (capacity > this.maxTableCapacity) {
            throw DYNAMIC_TABLE_CAPACITY_EXCEEDS_MAX;
        }
        this.dynamicTable.setCapacity(capacity);
    }

    void insertWithNameReference(QuicStreamChannel qpackDecoderStream, boolean staticTableRef, int nameIdx, CharSequence value) throws QpackException {
        QpackHeaderField entryForName = staticTableRef ? QpackStaticTable.getField(nameIdx) : this.dynamicTable.getEntryRelativeEncoderInstructions(nameIdx);
        this.dynamicTable.add(new QpackHeaderField(entryForName.name, value));
        this.sendInsertCountIncrementIfRequired(qpackDecoderStream);
    }

    void insertLiteral(QuicStreamChannel qpackDecoderStream, CharSequence name, CharSequence value) throws QpackException {
        this.dynamicTable.add(new QpackHeaderField(name, value));
        this.sendInsertCountIncrementIfRequired(qpackDecoderStream);
    }

    void duplicate(QuicStreamChannel qpackDecoderStream, int index) throws QpackException {
        this.dynamicTable.add(this.dynamicTable.getEntryRelativeEncoderInstructions(index));
        this.sendInsertCountIncrementIfRequired(qpackDecoderStream);
    }

    void streamAbandoned(QuicStreamChannel qpackDecoderStream, long streamId) {
        if (this.maxTableCapacity == 0L) {
            return;
        }
        ByteBuf cancel = qpackDecoderStream.alloc().buffer(8);
        QpackUtil.encodePrefixedInteger(cancel, (byte)64, 6, streamId);
        Http3CodecUtils.closeOnFailure(qpackDecoderStream.writeAndFlush(cancel));
    }

    private static boolean isIndexed(byte b) {
        return (b & 0x80) == 128;
    }

    private static boolean isLiteralWithNameRef(byte b) {
        return (b & 0xC0) == 64;
    }

    private static boolean isLiteral(byte b) {
        return (b & 0xE0) == 32;
    }

    private static boolean isIndexedWithPostBase(byte b) {
        return (b & 0xF0) == 16;
    }

    private static boolean isLiteralWithPostBaseNameRef(byte b) {
        return (b & 0xF0) == 0;
    }

    private void decodeIndexed(ByteBuf in, BiConsumer<CharSequence, CharSequence> sink, int base) throws QpackException {
        QpackHeaderField field;
        if (QpackUtil.firstByteEquals(in, (byte)-64)) {
            int idx = QpackUtil.decodePrefixedIntegerAsInt(in, 6);
            assert (idx >= 0);
            if (idx >= QpackStaticTable.length) {
                throw HEADER_ILLEGAL_INDEX_VALUE;
            }
            field = QpackStaticTable.getField(idx);
        } else {
            int idx = QpackUtil.decodePrefixedIntegerAsInt(in, 6);
            assert (idx >= 0);
            field = this.dynamicTable.getEntryRelativeEncodedField(base - idx - 1);
        }
        sink.accept(field.name, field.value);
    }

    private void decodeIndexedWithPostBase(ByteBuf in, BiConsumer<CharSequence, CharSequence> sink, int base) throws QpackException {
        int idx = QpackUtil.decodePrefixedIntegerAsInt(in, 4);
        assert (idx >= 0);
        QpackHeaderField field = this.dynamicTable.getEntryRelativeEncodedField(base + idx);
        sink.accept(field.name, field.value);
    }

    private void decodeLiteralWithNameRef(ByteBuf in, BiConsumer<CharSequence, CharSequence> sink, int base) throws QpackException {
        CharSequence name;
        int idx;
        if (QpackUtil.firstByteEquals(in, (byte)16)) {
            idx = QpackUtil.decodePrefixedIntegerAsInt(in, 4);
            assert (idx >= 0);
            if (idx >= QpackStaticTable.length) {
                throw NAME_ILLEGAL_INDEX_VALUE;
            }
            name = QpackStaticTable.getField((int)idx).name;
        } else {
            idx = QpackUtil.decodePrefixedIntegerAsInt(in, 4);
            assert (idx >= 0);
            name = this.dynamicTable.getEntryRelativeEncodedField((int)(base - idx - 1)).name;
        }
        CharSequence value = this.decodeHuffmanEncodedLiteral(in, 7);
        sink.accept(name, value);
    }

    private void decodeLiteralWithPostBaseNameRef(ByteBuf in, BiConsumer<CharSequence, CharSequence> sink, int base) throws QpackException {
        int idx = QpackUtil.decodePrefixedIntegerAsInt(in, 3);
        assert (idx >= 0);
        CharSequence name = this.dynamicTable.getEntryRelativeEncodedField((int)(base + idx)).name;
        CharSequence value = this.decodeHuffmanEncodedLiteral(in, 7);
        sink.accept(name, value);
    }

    private void decodeLiteral(ByteBuf in, BiConsumer<CharSequence, CharSequence> sink) throws QpackException {
        CharSequence name = this.decodeHuffmanEncodedLiteral(in, 3);
        CharSequence value = this.decodeHuffmanEncodedLiteral(in, 7);
        sink.accept(name, value);
    }

    private CharSequence decodeHuffmanEncodedLiteral(ByteBuf in, int prefix) throws QpackException {
        assert (prefix < 8);
        boolean huffmanEncoded = QpackUtil.firstByteEquals(in, (byte)(1 << prefix));
        int length = QpackUtil.decodePrefixedIntegerAsInt(in, prefix);
        assert (length >= 0);
        if (huffmanEncoded) {
            return this.huffmanDecoder.decode(in, length);
        }
        byte[] buf = new byte[length];
        in.readBytes(buf);
        return new AsciiString(buf, false);
    }

    int decodeRequiredInsertCount(QpackAttributes qpackAttributes, ByteBuf buf) throws QpackException {
        long encodedInsertCount = QpackUtil.decodePrefixedInteger(buf, 8);
        assert (encodedInsertCount >= 0L);
        if (encodedInsertCount == 0L) {
            return 0;
        }
        if (qpackAttributes.dynamicTableDisabled() || encodedInsertCount > this.fullRange) {
            throw INVALID_REQUIRED_INSERT_COUNT;
        }
        long maxValue = (long)this.dynamicTable.insertCount() + this.maxEntries;
        long maxWrapped = Math.floorDiv(maxValue, this.fullRange) * this.fullRange;
        long requiredInsertCount = maxWrapped + encodedInsertCount - 1L;
        if (requiredInsertCount > maxValue) {
            if (requiredInsertCount <= this.fullRange) {
                throw INVALID_REQUIRED_INSERT_COUNT;
            }
            requiredInsertCount -= this.fullRange;
        }
        if (requiredInsertCount == 0L) {
            throw INVALID_REQUIRED_INSERT_COUNT;
        }
        return QpackUtil.toIntOrThrow(requiredInsertCount);
    }

    int decodeBase(ByteBuf buf, int requiredInsertCount) throws QpackException {
        boolean s = (buf.getByte(buf.readerIndex()) & 0x80) == 128;
        int deltaBase = QpackUtil.decodePrefixedIntegerAsInt(buf, 7);
        assert (deltaBase >= 0);
        return s ? requiredInsertCount - deltaBase - 1 : requiredInsertCount + deltaBase;
    }

    private boolean shouldWaitForDynamicTableUpdates(int requiredInsertCount) throws QpackException {
        if (requiredInsertCount > this.dynamicTable.insertCount()) {
            if (this.blockedStreamsCount == this.maxBlockedStreams - 1) {
                throw MAX_BLOCKED_STREAMS_EXCEEDED;
            }
            return true;
        }
        return false;
    }

    private void sendInsertCountIncrementIfRequired(QuicStreamChannel qpackDecoderStream) throws QpackException {
        int insertCount = this.dynamicTable.insertCount();
        List<Runnable> runnables = this.blockedStreams.get(insertCount);
        if (runnables != null) {
            boolean failed = false;
            for (Runnable runnable : runnables) {
                try {
                    runnable.run();
                }
                catch (Exception e) {
                    failed = true;
                    logger.error("Failed to resume a blocked stream {}.", (Object)runnable, (Object)e);
                }
            }
            if (failed) {
                throw BLOCKED_STREAM_RESUMPTION_FAILED;
            }
        }
        if (this.stateSyncStrategy.entryAdded(insertCount)) {
            ByteBuf incr = qpackDecoderStream.alloc().buffer(8);
            QpackUtil.encodePrefixedInteger(incr, (byte)0, 6, (long)insertCount - this.lastAckInsertCount);
            this.lastAckInsertCount = insertCount;
            Http3CodecUtils.closeOnFailure(qpackDecoderStream.writeAndFlush(incr));
        }
    }
}

