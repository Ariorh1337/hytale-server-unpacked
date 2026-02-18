/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.BackgroundErrorReason;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.CompactionJobInfo;
import org.rocksdb.EventListener;
import org.rocksdb.ExternalFileIngestionInfo;
import org.rocksdb.FileOperationInfo;
import org.rocksdb.FlushJobInfo;
import org.rocksdb.MemTableInfo;
import org.rocksdb.RocksCallbackObject;
import org.rocksdb.RocksDB;
import org.rocksdb.Status;
import org.rocksdb.TableFileCreationBriefInfo;
import org.rocksdb.TableFileCreationInfo;
import org.rocksdb.TableFileDeletionInfo;
import org.rocksdb.WriteStallInfo;

public abstract class AbstractEventListener
extends RocksCallbackObject
implements EventListener {
    protected AbstractEventListener() {
        this(EnabledEventCallback.ON_FLUSH_COMPLETED, EnabledEventCallback.ON_FLUSH_BEGIN, EnabledEventCallback.ON_TABLE_FILE_DELETED, EnabledEventCallback.ON_COMPACTION_BEGIN, EnabledEventCallback.ON_COMPACTION_COMPLETED, EnabledEventCallback.ON_TABLE_FILE_CREATED, EnabledEventCallback.ON_TABLE_FILE_CREATION_STARTED, EnabledEventCallback.ON_MEMTABLE_SEALED, EnabledEventCallback.ON_COLUMN_FAMILY_HANDLE_DELETION_STARTED, EnabledEventCallback.ON_EXTERNAL_FILE_INGESTED, EnabledEventCallback.ON_BACKGROUND_ERROR, EnabledEventCallback.ON_STALL_CONDITIONS_CHANGED, EnabledEventCallback.ON_FILE_READ_FINISH, EnabledEventCallback.ON_FILE_WRITE_FINISH, EnabledEventCallback.ON_FILE_FLUSH_FINISH, EnabledEventCallback.ON_FILE_SYNC_FINISH, EnabledEventCallback.ON_FILE_RANGE_SYNC_FINISH, EnabledEventCallback.ON_FILE_TRUNCATE_FINISH, EnabledEventCallback.ON_FILE_CLOSE_FINISH, EnabledEventCallback.SHOULD_BE_NOTIFIED_ON_FILE_IO, EnabledEventCallback.ON_ERROR_RECOVERY_BEGIN, EnabledEventCallback.ON_ERROR_RECOVERY_COMPLETED);
    }

    protected AbstractEventListener(EnabledEventCallback ... enabledEventCallbackArray) {
        super(AbstractEventListener.packToLong(enabledEventCallbackArray));
    }

    private static long packToLong(EnabledEventCallback ... enabledEventCallbackArray) {
        long l = 0L;
        for (EnabledEventCallback enabledEventCallback : enabledEventCallbackArray) {
            l |= 1L << enabledEventCallback.getValue();
        }
        return l;
    }

    @Override
    public void onFlushCompleted(RocksDB rocksDB, FlushJobInfo flushJobInfo) {
    }

    private void onFlushCompletedProxy(long l, FlushJobInfo flushJobInfo) {
        RocksDB rocksDB = new RocksDB(l);
        rocksDB.disOwnNativeHandle();
        this.onFlushCompleted(rocksDB, flushJobInfo);
    }

    @Override
    public void onFlushBegin(RocksDB rocksDB, FlushJobInfo flushJobInfo) {
    }

    private void onFlushBeginProxy(long l, FlushJobInfo flushJobInfo) {
        RocksDB rocksDB = new RocksDB(l);
        rocksDB.disOwnNativeHandle();
        this.onFlushBegin(rocksDB, flushJobInfo);
    }

    @Override
    public void onTableFileDeleted(TableFileDeletionInfo tableFileDeletionInfo) {
    }

    @Override
    public void onCompactionBegin(RocksDB rocksDB, CompactionJobInfo compactionJobInfo) {
    }

    private void onCompactionBeginProxy(long l, CompactionJobInfo compactionJobInfo) {
        RocksDB rocksDB = new RocksDB(l);
        rocksDB.disOwnNativeHandle();
        this.onCompactionBegin(rocksDB, compactionJobInfo);
    }

    @Override
    public void onCompactionCompleted(RocksDB rocksDB, CompactionJobInfo compactionJobInfo) {
    }

    private void onCompactionCompletedProxy(long l, CompactionJobInfo compactionJobInfo) {
        RocksDB rocksDB = new RocksDB(l);
        rocksDB.disOwnNativeHandle();
        this.onCompactionCompleted(rocksDB, compactionJobInfo);
    }

    @Override
    public void onTableFileCreated(TableFileCreationInfo tableFileCreationInfo) {
    }

    @Override
    public void onTableFileCreationStarted(TableFileCreationBriefInfo tableFileCreationBriefInfo) {
    }

    @Override
    public void onMemTableSealed(MemTableInfo memTableInfo) {
    }

    @Override
    public void onColumnFamilyHandleDeletionStarted(ColumnFamilyHandle columnFamilyHandle) {
    }

    @Override
    public void onExternalFileIngested(RocksDB rocksDB, ExternalFileIngestionInfo externalFileIngestionInfo) {
    }

    private void onExternalFileIngestedProxy(long l, ExternalFileIngestionInfo externalFileIngestionInfo) {
        RocksDB rocksDB = new RocksDB(l);
        rocksDB.disOwnNativeHandle();
        this.onExternalFileIngested(rocksDB, externalFileIngestionInfo);
    }

    @Override
    public void onBackgroundError(BackgroundErrorReason backgroundErrorReason, Status status) {
    }

    private void onBackgroundErrorProxy(byte by, Status status) {
        this.onBackgroundError(BackgroundErrorReason.fromValue(by), status);
    }

    @Override
    public void onStallConditionsChanged(WriteStallInfo writeStallInfo) {
    }

    @Override
    public void onFileReadFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileWriteFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileFlushFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileSyncFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileRangeSyncFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileTruncateFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public void onFileCloseFinish(FileOperationInfo fileOperationInfo) {
    }

    @Override
    public boolean shouldBeNotifiedOnFileIO() {
        return false;
    }

    @Override
    public boolean onErrorRecoveryBegin(BackgroundErrorReason backgroundErrorReason, Status status) {
        return true;
    }

    private boolean onErrorRecoveryBeginProxy(byte by, Status status) {
        return this.onErrorRecoveryBegin(BackgroundErrorReason.fromValue(by), status);
    }

    @Override
    public void onErrorRecoveryCompleted(Status status) {
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewEventListener(lArray[0]);
    }

    @Override
    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    private native long createNewEventListener(long var1);

    private native void disposeInternal(long var1);

    public static enum EnabledEventCallback {
        ON_FLUSH_COMPLETED(0),
        ON_FLUSH_BEGIN(1),
        ON_TABLE_FILE_DELETED(2),
        ON_COMPACTION_BEGIN(3),
        ON_COMPACTION_COMPLETED(4),
        ON_TABLE_FILE_CREATED(5),
        ON_TABLE_FILE_CREATION_STARTED(6),
        ON_MEMTABLE_SEALED(7),
        ON_COLUMN_FAMILY_HANDLE_DELETION_STARTED(8),
        ON_EXTERNAL_FILE_INGESTED(9),
        ON_BACKGROUND_ERROR(10),
        ON_STALL_CONDITIONS_CHANGED(11),
        ON_FILE_READ_FINISH(12),
        ON_FILE_WRITE_FINISH(13),
        ON_FILE_FLUSH_FINISH(14),
        ON_FILE_SYNC_FINISH(15),
        ON_FILE_RANGE_SYNC_FINISH(16),
        ON_FILE_TRUNCATE_FINISH(17),
        ON_FILE_CLOSE_FINISH(18),
        SHOULD_BE_NOTIFIED_ON_FILE_IO(19),
        ON_ERROR_RECOVERY_BEGIN(20),
        ON_ERROR_RECOVERY_COMPLETED(21);

        private final byte value;

        private EnabledEventCallback(byte by) {
            this.value = by;
        }

        byte getValue() {
            return this.value;
        }

        static EnabledEventCallback fromValue(byte by) {
            for (EnabledEventCallback enabledEventCallback : EnabledEventCallback.values()) {
                if (enabledEventCallback.value != by) continue;
                return enabledEventCallback;
            }
            throw new IllegalArgumentException("Illegal value provided for EnabledEventCallback: " + by);
        }
    }
}

