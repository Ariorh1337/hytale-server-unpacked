package org.rocksdb;

public interface EventListener {
   void onFlushBegin(RocksDB var1, FlushJobInfo var2);

   void onFlushCompleted(RocksDB var1, FlushJobInfo var2);

   void onTableFileDeleted(TableFileDeletionInfo var1);

   void onCompactionBegin(RocksDB var1, CompactionJobInfo var2);

   void onCompactionCompleted(RocksDB var1, CompactionJobInfo var2);

   void onTableFileCreated(TableFileCreationInfo var1);

   void onTableFileCreationStarted(TableFileCreationBriefInfo var1);

   void onMemTableSealed(MemTableInfo var1);

   void onColumnFamilyHandleDeletionStarted(ColumnFamilyHandle var1);

   void onExternalFileIngested(RocksDB var1, ExternalFileIngestionInfo var2);

   void onBackgroundError(BackgroundErrorReason var1, Status var2);

   void onStallConditionsChanged(WriteStallInfo var1);

   void onFileReadFinish(FileOperationInfo var1);

   void onFileWriteFinish(FileOperationInfo var1);

   void onFileFlushFinish(FileOperationInfo var1);

   void onFileSyncFinish(FileOperationInfo var1);

   void onFileRangeSyncFinish(FileOperationInfo var1);

   void onFileTruncateFinish(FileOperationInfo var1);

   void onFileCloseFinish(FileOperationInfo var1);

   boolean shouldBeNotifiedOnFileIO();

   boolean onErrorRecoveryBegin(BackgroundErrorReason var1, Status var2);

   void onErrorRecoveryCompleted(Status var1);
}
