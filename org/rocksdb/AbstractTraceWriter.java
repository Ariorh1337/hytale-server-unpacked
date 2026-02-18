/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksCallbackObject;
import org.rocksdb.RocksDBException;
import org.rocksdb.Slice;
import org.rocksdb.Status;
import org.rocksdb.TraceWriter;

public abstract class AbstractTraceWriter
extends RocksCallbackObject
implements TraceWriter {
    public AbstractTraceWriter() {
        super(new long[0]);
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewTraceWriter();
    }

    private short writeProxy(long l) {
        try {
            this.write(new Slice(l));
            return AbstractTraceWriter.statusToShort(Status.Code.Ok, Status.SubCode.None);
        }
        catch (RocksDBException rocksDBException) {
            return AbstractTraceWriter.statusToShort(rocksDBException.getStatus());
        }
    }

    private short closeWriterProxy() {
        try {
            this.closeWriter();
            return AbstractTraceWriter.statusToShort(Status.Code.Ok, Status.SubCode.None);
        }
        catch (RocksDBException rocksDBException) {
            return AbstractTraceWriter.statusToShort(rocksDBException.getStatus());
        }
    }

    private static short statusToShort(Status status) {
        Status.Code code = status != null && status.getCode() != null ? status.getCode() : Status.Code.IOError;
        Status.SubCode subCode = status != null && status.getSubCode() != null ? status.getSubCode() : Status.SubCode.None;
        return AbstractTraceWriter.statusToShort(code, subCode);
    }

    private static short statusToShort(Status.Code code, Status.SubCode subCode) {
        short s = (short)(code.getValue() << 8);
        return (short)(s | subCode.getValue());
    }

    private native long createNewTraceWriter();
}

