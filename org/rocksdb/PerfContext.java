/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class PerfContext
extends RocksObject {
    protected PerfContext(long l) {
        super(l);
    }

    public void reset() {
        this.reset(this.nativeHandle_);
    }

    public long getUserKeyComparisonCount() {
        return this.getUserKeyComparisonCount(this.nativeHandle_);
    }

    public long getBlockCacheHitCount() {
        return this.getBlockCacheHitCount(this.nativeHandle_);
    }

    public long getBlockReadCount() {
        return this.getBlockReadCount(this.nativeHandle_);
    }

    public long getBlockReadByte() {
        return this.getBlockReadByte(this.nativeHandle_);
    }

    public long getBlockReadTime() {
        return this.getBlockReadTime(this.nativeHandle_);
    }

    public long getBlockReadCpuTime() {
        return this.getBlockReadCpuTime(this.nativeHandle_);
    }

    public long getBlockCacheIndexHitCount() {
        return this.getBlockCacheIndexHitCount(this.nativeHandle_);
    }

    public long getBlockCacheStandaloneHandleCount() {
        return this.getBlockCacheStandaloneHandleCount(this.nativeHandle_);
    }

    public long getBlockCacheRealHandleCount() {
        return this.getBlockCacheRealHandleCount(this.nativeHandle_);
    }

    public long getIndexBlockReadCount() {
        return this.getIndexBlockReadCount(this.nativeHandle_);
    }

    public long getBlockCacheFilterHitCount() {
        return this.getBlockCacheFilterHitCount(this.nativeHandle_);
    }

    public long getFilterBlockReadCount() {
        return this.getFilterBlockReadCount(this.nativeHandle_);
    }

    public long getCompressionDictBlockReadCount() {
        return this.getCompressionDictBlockReadCount(this.nativeHandle_);
    }

    public long getSecondaryCacheHitCount() {
        return this.getSecondaryCacheHitCount(this.nativeHandle_);
    }

    public long getCompressedSecCacheInsertRealCount() {
        return this.getCompressedSecCacheInsertRealCount(this.nativeHandle_);
    }

    public long getCompressedSecCacheInsertDummyCount() {
        return this.getCompressedSecCacheInsertDummyCount(this.nativeHandle_);
    }

    public long getCompressedSecCacheUncompressedBytes() {
        return this.getCompressedSecCacheUncompressedBytes(this.nativeHandle_);
    }

    public long getCompressedSecCacheCompressedBytes() {
        return this.getCompressedSecCacheCompressedBytes(this.nativeHandle_);
    }

    public long getBlockChecksumTime() {
        return this.getBlockChecksumTime(this.nativeHandle_);
    }

    public long getBlockDecompressTime() {
        return this.getBlockDecompressTime(this.nativeHandle_);
    }

    public long getReadBytes() {
        return this.getReadBytes(this.nativeHandle_);
    }

    public long getMultigetReadBytes() {
        return this.getMultigetReadBytes(this.nativeHandle_);
    }

    public long getIterReadBytes() {
        return this.getIterReadBytes(this.nativeHandle_);
    }

    public long getBlobCacheHitCount() {
        return this.getBlobCacheHitCount(this.nativeHandle_);
    }

    public long getBlobReadCount() {
        return this.getBlobReadCount(this.nativeHandle_);
    }

    public long getBlobReadByte() {
        return this.getBlobReadByte(this.nativeHandle_);
    }

    public long getBlobReadTime() {
        return this.getBlobReadTime(this.nativeHandle_);
    }

    public long getBlobChecksumTime() {
        return this.getBlobChecksumTime(this.nativeHandle_);
    }

    public long getBlobDecompressTime() {
        return this.getBlobDecompressTime(this.nativeHandle_);
    }

    public long getInternalKeySkippedCount() {
        return this.getInternalKeySkippedCount(this.nativeHandle_);
    }

    public long getInternalDeleteSkippedCount() {
        return this.getInternalDeleteSkippedCount(this.nativeHandle_);
    }

    public long getInternalRecentSkippedCount() {
        return this.getInternalRecentSkippedCount(this.nativeHandle_);
    }

    public long getInternalMergeCount() {
        return this.getInternalMergeCount(this.nativeHandle_);
    }

    public long getInternalMergePointLookupCount() {
        return this.getInternalMergePointLookupCount(this.nativeHandle_);
    }

    public long getInternalRangeDelReseekCount() {
        return this.getInternalRangeDelReseekCount(this.nativeHandle_);
    }

    public long getSnapshotTime() {
        return this.getSnapshotTime(this.nativeHandle_);
    }

    public long getFromMemtableTime() {
        return this.getFromMemtableTime(this.nativeHandle_);
    }

    public long getFromMemtableCount() {
        return this.getFromMemtableCount(this.nativeHandle_);
    }

    public long getPostProcessTime() {
        return this.getPostProcessTime(this.nativeHandle_);
    }

    public long getFromOutputFilesTime() {
        return this.getFromOutputFilesTime(this.nativeHandle_);
    }

    public long getSeekOnMemtableTime() {
        return this.getSeekOnMemtableTime(this.nativeHandle_);
    }

    public long getSeekOnMemtableCount() {
        return this.getSeekOnMemtableCount(this.nativeHandle_);
    }

    public long getNextOnMemtableCount() {
        return this.getNextOnMemtableCount(this.nativeHandle_);
    }

    public long getPrevOnMemtableCount() {
        return this.getPrevOnMemtableCount(this.nativeHandle_);
    }

    public long getSeekChildSeekTime() {
        return this.getSeekChildSeekTime(this.nativeHandle_);
    }

    public long getSeekChildSeekCount() {
        return this.getSeekChildSeekCount(this.nativeHandle_);
    }

    public long getSeekMinHeapTime() {
        return this.getSeekMinHeapTime(this.nativeHandle_);
    }

    public long getSeekMaxHeapTime() {
        return this.getSeekMaxHeapTime(this.nativeHandle_);
    }

    public long getSeekInternalSeekTime() {
        return this.getSeekInternalSeekTime(this.nativeHandle_);
    }

    public long getFindNextUserEntryTime() {
        return this.getFindNextUserEntryTime(this.nativeHandle_);
    }

    public long getWriteWalTime() {
        return this.getWriteWalTime(this.nativeHandle_);
    }

    public long getWriteMemtableTime() {
        return this.getWriteMemtableTime(this.nativeHandle_);
    }

    public long getWriteDelayTime() {
        return this.getWriteDelayTime(this.nativeHandle_);
    }

    public long getWriteSchedulingFlushesCompactionsTime() {
        return this.getWriteSchedulingFlushesCompactionsTime(this.nativeHandle_);
    }

    public long getWritePreAndPostProcessTime() {
        return this.getWritePreAndPostProcessTime(this.nativeHandle_);
    }

    public long getWriteThreadWaitNanos() {
        return this.getWriteThreadWaitNanos(this.nativeHandle_);
    }

    public long getDbMutexLockNanos() {
        return this.getDbMutexLockNanos(this.nativeHandle_);
    }

    public long getDbConditionWaitNanos() {
        return this.getDbConditionWaitNanos(this.nativeHandle_);
    }

    public long getMergeOperatorTimeNanos() {
        return this.getMergeOperatorTimeNanos(this.nativeHandle_);
    }

    public long getReadIndexBlockNanos() {
        return this.getReadIndexBlockNanos(this.nativeHandle_);
    }

    public long getReadFilterBlockNanos() {
        return this.getReadFilterBlockNanos(this.nativeHandle_);
    }

    public long getNewTableBlockIterNanos() {
        return this.getNewTableBlockIterNanos(this.nativeHandle_);
    }

    public long getNewTableIteratorNanos() {
        return this.getNewTableIteratorNanos(this.nativeHandle_);
    }

    public long getBlockSeekNanos() {
        return this.getBlockSeekNanos(this.nativeHandle_);
    }

    public long getFindTableNanos() {
        return this.getFindTableNanos(this.nativeHandle_);
    }

    public long getBloomMemtableHitCount() {
        return this.getBloomMemtableHitCount(this.nativeHandle_);
    }

    public long getBloomMemtableMissCount() {
        return this.getBloomMemtableMissCount(this.nativeHandle_);
    }

    public long getBloomSstHitCount() {
        return this.getBloomSstHitCount(this.nativeHandle_);
    }

    public long getBloomSstMissCount() {
        return this.getBloomSstMissCount(this.nativeHandle_);
    }

    public long getKeyLockWaitTime() {
        return this.getKeyLockWaitTime(this.nativeHandle_);
    }

    public long getKeyLockWaitCount() {
        return this.getKeyLockWaitCount(this.nativeHandle_);
    }

    public long getEnvNewSequentialFileNanos() {
        return this.getEnvNewSequentialFileNanos(this.nativeHandle_);
    }

    public long getEnvNewRandomAccessFileNanos() {
        return this.getEnvNewRandomAccessFileNanos(this.nativeHandle_);
    }

    public long getEnvNewWritableFileNanos() {
        return this.getEnvNewWritableFileNanos(this.nativeHandle_);
    }

    public long getEnvReuseWritableFileNanos() {
        return this.getEnvReuseWritableFileNanos(this.nativeHandle_);
    }

    public long getEnvNewRandomRwFileNanos() {
        return this.getEnvNewRandomRwFileNanos(this.nativeHandle_);
    }

    public long getEnvNewDirectoryNanos() {
        return this.getEnvNewDirectoryNanos(this.nativeHandle_);
    }

    public long getEnvFileExistsNanos() {
        return this.getEnvFileExistsNanos(this.nativeHandle_);
    }

    public long getEnvGetChildrenNanos() {
        return this.getEnvGetChildrenNanos(this.nativeHandle_);
    }

    public long getEnvGetChildrenFileAttributesNanos() {
        return this.getEnvGetChildrenFileAttributesNanos(this.nativeHandle_);
    }

    public long getEnvDeleteFileNanos() {
        return this.getEnvDeleteFileNanos(this.nativeHandle_);
    }

    public long getEnvCreateDirNanos() {
        return this.getEnvCreateDirNanos(this.nativeHandle_);
    }

    public long getEnvCreateDirIfMissingNanos() {
        return this.getEnvCreateDirIfMissingNanos(this.nativeHandle_);
    }

    public long getEnvDeleteDirNanos() {
        return this.getEnvDeleteDirNanos(this.nativeHandle_);
    }

    public long getEnvGetFileSizeNanos() {
        return this.getEnvGetFileSizeNanos(this.nativeHandle_);
    }

    public long getEnvGetFileModificationTimeNanos() {
        return this.getEnvGetFileModificationTimeNanos(this.nativeHandle_);
    }

    public long getEnvRenameFileNanos() {
        return this.getEnvRenameFileNanos(this.nativeHandle_);
    }

    public long getEnvLinkFileNanos() {
        return this.getEnvLinkFileNanos(this.nativeHandle_);
    }

    public long getEnvLockFileNanos() {
        return this.getEnvLockFileNanos(this.nativeHandle_);
    }

    public long getEnvUnlockFileNanos() {
        return this.getEnvUnlockFileNanos(this.nativeHandle_);
    }

    public long getEnvNewLoggerNanos() {
        return this.getEnvNewLoggerNanos(this.nativeHandle_);
    }

    public long getGetCpuNanos() {
        return this.getGetCpuNanos(this.nativeHandle_);
    }

    public long getIterNextCpuNanos() {
        return this.getIterNextCpuNanos(this.nativeHandle_);
    }

    public long getIterPrevCpuNanos() {
        return this.getIterPrevCpuNanos(this.nativeHandle_);
    }

    public long getIterSeekCpuNanos() {
        return this.getIterSeekCpuNanos(this.nativeHandle_);
    }

    public long getEncryptDataNanos() {
        return this.getEncryptDataNanos(this.nativeHandle_);
    }

    public long getDecryptDataNanos() {
        return this.getDecryptDataNanos(this.nativeHandle_);
    }

    public long getNumberAsyncSeek() {
        return this.getNumberAsyncSeek(this.nativeHandle_);
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean bl) {
        return this.toString(this.nativeHandle_, bl);
    }

    @Override
    protected void disposeInternal(long l) {
    }

    private native void reset(long var1);

    private native long getUserKeyComparisonCount(long var1);

    private native long getBlockCacheHitCount(long var1);

    private native long getBlockReadCount(long var1);

    private native long getBlockReadByte(long var1);

    private native long getBlockReadTime(long var1);

    private native long getBlockReadCpuTime(long var1);

    private native long getBlockCacheIndexHitCount(long var1);

    private native long getBlockCacheStandaloneHandleCount(long var1);

    private native long getBlockCacheRealHandleCount(long var1);

    private native long getIndexBlockReadCount(long var1);

    private native long getBlockCacheFilterHitCount(long var1);

    private native long getFilterBlockReadCount(long var1);

    private native long getCompressionDictBlockReadCount(long var1);

    private native long getSecondaryCacheHitCount(long var1);

    private native long getCompressedSecCacheInsertRealCount(long var1);

    private native long getCompressedSecCacheInsertDummyCount(long var1);

    private native long getCompressedSecCacheUncompressedBytes(long var1);

    private native long getCompressedSecCacheCompressedBytes(long var1);

    private native long getBlockChecksumTime(long var1);

    private native long getBlockDecompressTime(long var1);

    private native long getReadBytes(long var1);

    private native long getMultigetReadBytes(long var1);

    private native long getIterReadBytes(long var1);

    private native long getBlobCacheHitCount(long var1);

    private native long getBlobReadCount(long var1);

    private native long getBlobReadByte(long var1);

    private native long getBlobReadTime(long var1);

    private native long getBlobChecksumTime(long var1);

    private native long getBlobDecompressTime(long var1);

    private native long getInternalKeySkippedCount(long var1);

    private native long getInternalDeleteSkippedCount(long var1);

    private native long getInternalRecentSkippedCount(long var1);

    private native long getInternalMergeCount(long var1);

    private native long getInternalMergePointLookupCount(long var1);

    private native long getInternalRangeDelReseekCount(long var1);

    private native long getSnapshotTime(long var1);

    private native long getFromMemtableTime(long var1);

    private native long getFromMemtableCount(long var1);

    private native long getPostProcessTime(long var1);

    private native long getFromOutputFilesTime(long var1);

    private native long getSeekOnMemtableTime(long var1);

    private native long getSeekOnMemtableCount(long var1);

    private native long getNextOnMemtableCount(long var1);

    private native long getPrevOnMemtableCount(long var1);

    private native long getSeekChildSeekTime(long var1);

    private native long getSeekChildSeekCount(long var1);

    private native long getSeekMinHeapTime(long var1);

    private native long getSeekMaxHeapTime(long var1);

    private native long getSeekInternalSeekTime(long var1);

    private native long getFindNextUserEntryTime(long var1);

    private native long getWriteWalTime(long var1);

    private native long getWriteMemtableTime(long var1);

    private native long getWriteDelayTime(long var1);

    private native long getWriteSchedulingFlushesCompactionsTime(long var1);

    private native long getWritePreAndPostProcessTime(long var1);

    private native long getWriteThreadWaitNanos(long var1);

    private native long getDbMutexLockNanos(long var1);

    private native long getDbConditionWaitNanos(long var1);

    private native long getMergeOperatorTimeNanos(long var1);

    private native long getReadIndexBlockNanos(long var1);

    private native long getReadFilterBlockNanos(long var1);

    private native long getNewTableBlockIterNanos(long var1);

    private native long getNewTableIteratorNanos(long var1);

    private native long getBlockSeekNanos(long var1);

    private native long getFindTableNanos(long var1);

    private native long getBloomMemtableHitCount(long var1);

    private native long getBloomMemtableMissCount(long var1);

    private native long getBloomSstHitCount(long var1);

    private native long getBloomSstMissCount(long var1);

    private native long getKeyLockWaitTime(long var1);

    private native long getKeyLockWaitCount(long var1);

    private native long getEnvNewSequentialFileNanos(long var1);

    private native long getEnvNewRandomAccessFileNanos(long var1);

    private native long getEnvNewWritableFileNanos(long var1);

    private native long getEnvReuseWritableFileNanos(long var1);

    private native long getEnvNewRandomRwFileNanos(long var1);

    private native long getEnvNewDirectoryNanos(long var1);

    private native long getEnvFileExistsNanos(long var1);

    private native long getEnvGetChildrenNanos(long var1);

    private native long getEnvGetChildrenFileAttributesNanos(long var1);

    private native long getEnvDeleteFileNanos(long var1);

    private native long getEnvCreateDirNanos(long var1);

    private native long getEnvCreateDirIfMissingNanos(long var1);

    private native long getEnvDeleteDirNanos(long var1);

    private native long getEnvGetFileSizeNanos(long var1);

    private native long getEnvGetFileModificationTimeNanos(long var1);

    private native long getEnvRenameFileNanos(long var1);

    private native long getEnvLinkFileNanos(long var1);

    private native long getEnvLockFileNanos(long var1);

    private native long getEnvUnlockFileNanos(long var1);

    private native long getEnvNewLoggerNanos(long var1);

    private native long getGetCpuNanos(long var1);

    private native long getIterNextCpuNanos(long var1);

    private native long getIterPrevCpuNanos(long var1);

    private native long getIterSeekCpuNanos(long var1);

    private native long getEncryptDataNanos(long var1);

    private native long getDecryptDataNanos(long var1);

    private native long getNumberAsyncSeek(long var1);

    private native String toString(long var1, boolean var3);
}

