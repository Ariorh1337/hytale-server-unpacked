/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.Status;

public class ByteBufferGetStatus {
    public final Status status;
    public final int requiredSize;
    public final ByteBuffer value;

    ByteBufferGetStatus(Status status, int n, ByteBuffer byteBuffer) {
        this.status = status;
        this.requiredSize = n;
        this.value = byteBuffer;
    }

    ByteBufferGetStatus(Status status) {
        this.status = status;
        this.requiredSize = 0;
        this.value = null;
    }
}

