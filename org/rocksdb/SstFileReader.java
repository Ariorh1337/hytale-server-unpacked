/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;
import org.rocksdb.SstFileReaderIterator;
import org.rocksdb.TableProperties;

public class SstFileReader
extends RocksObject {
    public SstFileReader(Options options) {
        super(SstFileReader.newSstFileReader(options.nativeHandle_));
    }

    public SstFileReaderIterator newIterator(ReadOptions readOptions) {
        assert (this.isOwningHandle());
        long l = SstFileReader.newIterator(this.nativeHandle_, readOptions.nativeHandle_);
        return new SstFileReaderIterator(this, l);
    }

    public void open(String string) throws RocksDBException {
        SstFileReader.open(this.nativeHandle_, string);
    }

    public void verifyChecksum() throws RocksDBException {
        SstFileReader.verifyChecksum(this.nativeHandle_);
    }

    public TableProperties getTableProperties() throws RocksDBException {
        return SstFileReader.getTableProperties(this.nativeHandle_);
    }

    @Override
    protected final void disposeInternal(long l) {
        SstFileReader.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native long newIterator(long var0, long var2);

    private static native void open(long var0, String var2) throws RocksDBException;

    private static native long newSstFileReader(long var0);

    private static native void verifyChecksum(long var0) throws RocksDBException;

    private static native TableProperties getTableProperties(long var0) throws RocksDBException;
}

