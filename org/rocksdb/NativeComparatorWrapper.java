/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorType;

public abstract class NativeComparatorWrapper
extends AbstractComparator {
    static final String NATIVE_CODE_IMPLEMENTATION_SHOULD_NOT_BE_CALLED = "This should not be called. Implementation is in Native code";

    @Override
    final ComparatorType getComparatorType() {
        return ComparatorType.JAVA_NATIVE_COMPARATOR_WRAPPER;
    }

    @Override
    public final String name() {
        throw new IllegalStateException(NATIVE_CODE_IMPLEMENTATION_SHOULD_NOT_BE_CALLED);
    }

    @Override
    public final int compare(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        throw new IllegalStateException(NATIVE_CODE_IMPLEMENTATION_SHOULD_NOT_BE_CALLED);
    }

    @Override
    public final void findShortestSeparator(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        throw new IllegalStateException(NATIVE_CODE_IMPLEMENTATION_SHOULD_NOT_BE_CALLED);
    }

    @Override
    public final void findShortSuccessor(ByteBuffer byteBuffer) {
        throw new IllegalStateException(NATIVE_CODE_IMPLEMENTATION_SHOULD_NOT_BE_CALLED);
    }

    @Override
    protected void disposeInternal() {
        NativeComparatorWrapper.disposeInternal(this.nativeHandle_);
    }

    private static native void disposeInternal(long var0);
}

