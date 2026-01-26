/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.QpackDecoder;
import io.netty.handler.codec.http3.QpackException;
import io.netty.handler.codec.http3.QpackHeaderField;
import io.netty.handler.codec.http3.QpackUtil;
import io.netty.util.AsciiString;
import io.netty.util.internal.MathUtil;
import org.jetbrains.annotations.Nullable;

final class QpackEncoderDynamicTable {
    private static final QpackException INVALID_KNOW_RECEIVED_COUNT_INCREMENT = QpackException.newStatic(QpackDecoder.class, "incrementKnownReceivedCount(...)", "QPACK - invalid known received count increment.");
    private static final QpackException INVALID_REQUIRED_INSERT_COUNT_INCREMENT = QpackException.newStatic(QpackDecoder.class, "acknowledgeInsertCount(...)", "QPACK - invalid required insert count acknowledgment.");
    private static final QpackException INVALID_TABLE_CAPACITY = QpackException.newStatic(QpackDecoder.class, "validateCapacity(...)", "QPACK - dynamic table capacity is invalid.");
    private static final QpackException CAPACITY_ALREADY_SET = QpackException.newStatic(QpackDecoder.class, "maxTableCapacity(...)", "QPACK - dynamic table capacity is already set.");
    public static final int NOT_FOUND = Integer.MIN_VALUE;
    private final HeaderEntry[] fields;
    private final int expectedFreeCapacityPercentage;
    private final byte hashMask;
    private long size;
    private long maxTableCapacity = -1L;
    private final HeaderEntry head;
    private HeaderEntry drain;
    private HeaderEntry knownReceived;
    private HeaderEntry tail;

    QpackEncoderDynamicTable() {
        this(16, 10);
    }

    QpackEncoderDynamicTable(int arraySizeHint, int expectedFreeCapacityPercentage) {
        this.fields = new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
        this.hashMask = (byte)(this.fields.length - 1);
        this.head = new HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, -1, null);
        this.expectedFreeCapacityPercentage = expectedFreeCapacityPercentage;
        this.resetIndicesToHead();
    }

    int add(CharSequence name, CharSequence value, long headerSize) {
        HeaderEntry e;
        if (this.maxTableCapacity - this.size < headerSize) {
            return -1;
        }
        if (this.tail.index == Integer.MAX_VALUE) {
            this.evictUnreferencedEntries();
            return -1;
        }
        int h = AsciiString.hashCode(name);
        int i = this.index(h);
        HeaderEntry old = this.fields[i];
        this.fields[i] = e = new HeaderEntry(h, name, value, this.tail.index + 1, old);
        e.addNextTo(this.tail);
        this.tail = e;
        this.size += headerSize;
        this.ensureFreeCapacity();
        return e.index;
    }

    void acknowledgeInsertCountOnAck(int entryIndex) throws QpackException {
        this.acknowledgeInsertCount(entryIndex, true);
    }

    void acknowledgeInsertCountOnCancellation(int entryIndex) throws QpackException {
        this.acknowledgeInsertCount(entryIndex, false);
    }

    private void acknowledgeInsertCount(int entryIndex, boolean updateKnownReceived) throws QpackException {
        if (entryIndex < 0) {
            throw INVALID_REQUIRED_INSERT_COUNT_INCREMENT;
        }
        HeaderEntry e = this.head.next;
        while (e != null) {
            if (e.index == entryIndex) {
                assert (e.refCount > 0);
                --e.refCount;
                if (updateKnownReceived && e.index > this.knownReceived.index) {
                    this.knownReceived = e;
                }
                this.evictUnreferencedEntries();
                return;
            }
            e = e.next;
        }
        throw INVALID_REQUIRED_INSERT_COUNT_INCREMENT;
    }

    void incrementKnownReceivedCount(int knownReceivedCountIncr) throws QpackException {
        if (knownReceivedCountIncr <= 0) {
            throw INVALID_KNOW_RECEIVED_COUNT_INCREMENT;
        }
        while (this.knownReceived.next != null && knownReceivedCountIncr > 0) {
            this.knownReceived = this.knownReceived.next;
            --knownReceivedCountIncr;
        }
        if (knownReceivedCountIncr == 0) {
            this.evictUnreferencedEntries();
            return;
        }
        throw INVALID_KNOW_RECEIVED_COUNT_INCREMENT;
    }

    int insertCount() {
        return this.tail.index + 1;
    }

    int encodedRequiredInsertCount(int reqInsertCount) {
        return reqInsertCount == 0 ? 0 : reqInsertCount % Math.toIntExact(2L * QpackUtil.maxEntries(this.maxTableCapacity)) + 1;
    }

    int encodedKnownReceivedCount() {
        return this.encodedRequiredInsertCount(this.knownReceived.index + 1);
    }

    void maxTableCapacity(long capacity) throws QpackException {
        QpackEncoderDynamicTable.validateCapacity(capacity);
        if (this.maxTableCapacity >= 0L) {
            throw CAPACITY_ALREADY_SET;
        }
        this.maxTableCapacity = capacity;
    }

    int relativeIndexForEncoderInstructions(int entryIndex) {
        assert (entryIndex >= 0);
        assert (entryIndex <= this.tail.index);
        return this.tail.index - entryIndex;
    }

    int getEntryIndex(@Nullable CharSequence name, @Nullable CharSequence value) {
        if (this.tail != this.head && name != null && value != null) {
            int h = AsciiString.hashCode(name);
            int i = this.index(h);
            HeaderEntry firstNameMatch = null;
            HeaderEntry entry = null;
            HeaderEntry e = this.fields[i];
            while (e != null) {
                if (e.hash == h && QpackUtil.equalsVariableTime(value, e.value)) {
                    if (QpackUtil.equalsVariableTime(name, e.name)) {
                        entry = e;
                        break;
                    }
                } else if (firstNameMatch == null && QpackUtil.equalsVariableTime(name, e.name)) {
                    firstNameMatch = e;
                }
                e = e.nextSibling;
            }
            if (entry != null) {
                return entry.index;
            }
            if (firstNameMatch != null) {
                return -firstNameMatch.index - 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    int addReferenceToEntry(@Nullable CharSequence name, @Nullable CharSequence value, int idx) {
        if (this.tail != this.head && name != null && value != null) {
            int h = AsciiString.hashCode(name);
            int i = this.index(h);
            HeaderEntry e = this.fields[i];
            while (e != null) {
                if (e.hash == h && idx == e.index) {
                    ++e.refCount;
                    return e.index + 1;
                }
                e = e.nextSibling;
            }
        }
        throw new IllegalArgumentException("Index " + idx + " not found");
    }

    boolean requiresDuplication(int idx, long size) {
        assert (this.head != this.tail);
        if (this.size + size > this.maxTableCapacity || this.head == this.drain) {
            return false;
        }
        return idx >= this.head.next.index && idx <= this.drain.index;
    }

    private void evictUnreferencedEntries() {
        if (this.head == this.knownReceived || this.head == this.drain) {
            return;
        }
        while (this.head.next != null && this.head.next != this.knownReceived.next && this.head.next != this.drain.next) {
            if (this.removeIfUnreferenced()) continue;
            return;
        }
    }

    private boolean removeIfUnreferenced() {
        HeaderEntry toRemove = this.head.next;
        if (toRemove.refCount != 0) {
            return false;
        }
        this.size -= toRemove.size();
        int i = this.index(toRemove.hash);
        HeaderEntry e = this.fields[i];
        HeaderEntry prev = null;
        while (e != null && e != toRemove) {
            prev = e;
            e = e.nextSibling;
        }
        if (e == toRemove) {
            if (prev == null) {
                this.fields[i] = e.nextSibling;
            } else {
                prev.nextSibling = e.nextSibling;
            }
        }
        toRemove.remove(this.head);
        if (toRemove == this.tail) {
            this.resetIndicesToHead();
        }
        if (toRemove == this.drain) {
            this.drain = this.head;
        }
        if (toRemove == this.knownReceived) {
            this.knownReceived = this.head;
        }
        return true;
    }

    private void resetIndicesToHead() {
        this.tail = this.head;
        this.drain = this.head;
        this.knownReceived = this.head;
    }

    private void ensureFreeCapacity() {
        long cSize;
        long maxDesiredSize = Math.max(32L, (long)(100 - this.expectedFreeCapacityPercentage) * this.maxTableCapacity / 100L);
        HeaderEntry nDrain = this.head;
        for (cSize = this.size; nDrain.next != null && cSize > maxDesiredSize; cSize -= nDrain.next.size()) {
            nDrain = nDrain.next;
        }
        if (cSize != this.size) {
            this.drain = nDrain;
            this.evictUnreferencedEntries();
        }
    }

    private int index(int h) {
        return h & this.hashMask;
    }

    private static void validateCapacity(long capacity) throws QpackException {
        if (capacity < 0L || capacity > 0xFFFFFFFFL) {
            throw INVALID_TABLE_CAPACITY;
        }
    }

    private static final class HeaderEntry
    extends QpackHeaderField {
        HeaderEntry next;
        HeaderEntry nextSibling;
        int refCount;
        final int hash;
        final int index;

        HeaderEntry(int hash, CharSequence name, CharSequence value, int index, @Nullable HeaderEntry nextSibling) {
            super(name, value);
            this.index = index;
            this.hash = hash;
            this.nextSibling = nextSibling;
        }

        void remove(HeaderEntry prev) {
            assert (prev != this);
            prev.next = this.next;
            this.next = null;
            this.nextSibling = null;
        }

        void addNextTo(HeaderEntry prev) {
            assert (prev != this);
            this.next = prev.next;
            prev.next = this;
        }
    }
}

