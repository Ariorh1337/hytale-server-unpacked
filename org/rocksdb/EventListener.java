/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.BackgroundErrorReason;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.CompactionJobInfo;
import org.rocksdb.ExternalFileIngestionInfo;
import org.rocksdb.FileOperationInfo;
import org.rocksdb.FlushJobInfo;
import org.rocksdb.MemTableInfo;
import org.rocksdb.RocksDB;
import org.rocksdb.Status;
import org.rocksdb.TableFileCreationBriefInfo;
import org.rocksdb.TableFileCreationInfo;
import org.rocksdb.TableFileDeletionInfo;
import org.rocksdb.WriteStallInfo;

public interface EventListener {
    public void onFlushBegin(RocksDB var1, FlushJobInfo var2);

    public void onFlushCompleted(RocksDB var1, FlushJobInfo var2);

    public void onTableFileDeleted(TableFileDeletionInfo var1);

    public void onCompactionBegin(RocksDB var1, CompactionJobInfo var2);

    public void onCompactionCompleted(RocksDB var1, CompactionJobInfo var2);

    public void onTableFileCreated(TableFileCreationInfo var1);

    public void onTableFileCreationStarted(TableFileCreationBriefInfo var1);

    public void onMemTableSealed(MemTableInfo var1);

    public void onColumnFamilyHandleDeletionStarted(ColumnFamilyHandle var1);

    public void onExternalFileIngested(RocksDB var1, ExternalFileIngestionInfo var2);

    public void onBackgroundError(BackgroundErrorReason var1, Status var2);

    public void onStallConditionsChanged(WriteStallInfo var1);

    public void onFileReadFinish(FileOperationInfo var1);

    public void onFileWriteFinish(FileOperationInfo var1);

    public void onFileFlushFinish(FileOperationInfo var1);

    public void onFileSyncFinish(FileOperationInfo var1);

    public void onFileRangeSyncFinish(FileOperationInfo var1);

    public void onFileTruncateFinish(FileOperationInfo var1);

    public void onFileCloseFinish(FileOperationInfo var1);

    public boolean shouldBeNotifiedOnFileIO();

    public boolean onErrorRecoveryBegin(BackgroundErrorReason var1, Status var2);

    public void onErrorRecoveryCompleted(Status var1);
}

