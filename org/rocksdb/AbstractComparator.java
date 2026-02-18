/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.ComparatorOptions;
import org.rocksdb.ComparatorType;
import org.rocksdb.RocksCallbackObject;

public abstract class AbstractComparator
extends RocksCallbackObject {
    AbstractComparator() {
        super(new long[0]);
    }

    protected AbstractComparator(ComparatorOptions comparatorOptions) {
        super(comparatorOptions.nativeHandle_);
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewComparator(lArray[0]);
    }

    ComparatorType getComparatorType() {
        return ComparatorType.JAVA_COMPARATOR;
    }

    public abstract String name();

    public abstract int compare(ByteBuffer var1, ByteBuffer var2);

    public void findShortestSeparator(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
    }

    public void findShortSuccessor(ByteBuffer byteBuffer) {
    }

    public final boolean usingDirectBuffers() {
        return AbstractComparator.usingDirectBuffers(this.nativeHandle_);
    }

    private static native boolean usingDirectBuffers(long var0);

    private native long createNewComparator(long var1);
}

