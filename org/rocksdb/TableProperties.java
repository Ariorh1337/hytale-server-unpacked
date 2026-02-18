/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class TableProperties {
    private final long dataSize;
    private final long indexSize;
    private final long indexPartitions;
    private final long topLevelIndexSize;
    private final long indexKeyIsUserKey;
    private final long indexValueIsDeltaEncoded;
    private final long filterSize;
    private final long rawKeySize;
    private final long rawValueSize;
    private final long numDataBlocks;
    private final long numEntries;
    private final long numDeletions;
    private final long numMergeOperands;
    private final long numRangeDeletions;
    private final long formatVersion;
    private final long fixedKeyLen;
    private final long columnFamilyId;
    private final long creationTime;
    private final long oldestKeyTime;
    private final long slowCompressionEstimatedDataSize;
    private final long fastCompressionEstimatedDataSize;
    private final long externalSstFileGlobalSeqnoOffset;
    private final byte[] columnFamilyName;
    private final String filterPolicyName;
    private final String comparatorName;
    private final String mergeOperatorName;
    private final String prefixExtractorName;
    private final String propertyCollectorsNames;
    private final String compressionName;
    private final Map<String, String> userCollectedProperties;
    private final Map<String, String> readableProperties;

    TableProperties(long l, long l2, long l3, long l4, long l5, long l6, long l7, long l8, long l9, long l10, long l11, long l12, long l13, long l14, long l15, long l16, long l17, long l18, long l19, long l20, long l21, long l22, byte[] byArray, String string, String string2, String string3, String string4, String string5, String string6, Map<String, String> map, Map<String, String> map2) {
        this.dataSize = l;
        this.indexSize = l2;
        this.indexPartitions = l3;
        this.topLevelIndexSize = l4;
        this.indexKeyIsUserKey = l5;
        this.indexValueIsDeltaEncoded = l6;
        this.filterSize = l7;
        this.rawKeySize = l8;
        this.rawValueSize = l9;
        this.numDataBlocks = l10;
        this.numEntries = l11;
        this.numDeletions = l12;
        this.numMergeOperands = l13;
        this.numRangeDeletions = l14;
        this.formatVersion = l15;
        this.fixedKeyLen = l16;
        this.columnFamilyId = l17;
        this.creationTime = l18;
        this.oldestKeyTime = l19;
        this.slowCompressionEstimatedDataSize = l20;
        this.fastCompressionEstimatedDataSize = l21;
        this.externalSstFileGlobalSeqnoOffset = l22;
        this.columnFamilyName = byArray;
        this.filterPolicyName = string;
        this.comparatorName = string2;
        this.mergeOperatorName = string3;
        this.prefixExtractorName = string4;
        this.propertyCollectorsNames = string5;
        this.compressionName = string6;
        this.userCollectedProperties = map;
        this.readableProperties = map2;
    }

    public long getDataSize() {
        return this.dataSize;
    }

    public long getIndexSize() {
        return this.indexSize;
    }

    public long getIndexPartitions() {
        return this.indexPartitions;
    }

    public long getTopLevelIndexSize() {
        return this.topLevelIndexSize;
    }

    public long getIndexKeyIsUserKey() {
        return this.indexKeyIsUserKey;
    }

    public long getIndexValueIsDeltaEncoded() {
        return this.indexValueIsDeltaEncoded;
    }

    public long getFilterSize() {
        return this.filterSize;
    }

    public long getRawKeySize() {
        return this.rawKeySize;
    }

    public long getRawValueSize() {
        return this.rawValueSize;
    }

    public long getNumDataBlocks() {
        return this.numDataBlocks;
    }

    public long getNumEntries() {
        return this.numEntries;
    }

    public long getNumDeletions() {
        return this.numDeletions;
    }

    public long getNumMergeOperands() {
        return this.numMergeOperands;
    }

    public long getNumRangeDeletions() {
        return this.numRangeDeletions;
    }

    public long getFormatVersion() {
        return this.formatVersion;
    }

    public long getFixedKeyLen() {
        return this.fixedKeyLen;
    }

    public long getColumnFamilyId() {
        return this.columnFamilyId;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getOldestKeyTime() {
        return this.oldestKeyTime;
    }

    public long getSlowCompressionEstimatedDataSize() {
        return this.slowCompressionEstimatedDataSize;
    }

    public long getFastCompressionEstimatedDataSize() {
        return this.fastCompressionEstimatedDataSize;
    }

    public byte[] getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public String getFilterPolicyName() {
        return this.filterPolicyName;
    }

    public String getComparatorName() {
        return this.comparatorName;
    }

    public String getMergeOperatorName() {
        return this.mergeOperatorName;
    }

    public String getPrefixExtractorName() {
        return this.prefixExtractorName;
    }

    public String getPropertyCollectorsNames() {
        return this.propertyCollectorsNames;
    }

    public String getCompressionName() {
        return this.compressionName;
    }

    public Map<String, String> getUserCollectedProperties() {
        return this.userCollectedProperties;
    }

    public Map<String, String> getReadableProperties() {
        return this.readableProperties;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TableProperties tableProperties = (TableProperties)object;
        return this.dataSize == tableProperties.dataSize && this.indexSize == tableProperties.indexSize && this.indexPartitions == tableProperties.indexPartitions && this.topLevelIndexSize == tableProperties.topLevelIndexSize && this.indexKeyIsUserKey == tableProperties.indexKeyIsUserKey && this.indexValueIsDeltaEncoded == tableProperties.indexValueIsDeltaEncoded && this.filterSize == tableProperties.filterSize && this.rawKeySize == tableProperties.rawKeySize && this.rawValueSize == tableProperties.rawValueSize && this.numDataBlocks == tableProperties.numDataBlocks && this.numEntries == tableProperties.numEntries && this.numDeletions == tableProperties.numDeletions && this.numMergeOperands == tableProperties.numMergeOperands && this.numRangeDeletions == tableProperties.numRangeDeletions && this.formatVersion == tableProperties.formatVersion && this.fixedKeyLen == tableProperties.fixedKeyLen && this.columnFamilyId == tableProperties.columnFamilyId && this.creationTime == tableProperties.creationTime && this.oldestKeyTime == tableProperties.oldestKeyTime && this.slowCompressionEstimatedDataSize == tableProperties.slowCompressionEstimatedDataSize && this.fastCompressionEstimatedDataSize == tableProperties.fastCompressionEstimatedDataSize && this.externalSstFileGlobalSeqnoOffset == tableProperties.externalSstFileGlobalSeqnoOffset && Arrays.equals(this.columnFamilyName, tableProperties.columnFamilyName) && Objects.equals(this.filterPolicyName, tableProperties.filterPolicyName) && Objects.equals(this.comparatorName, tableProperties.comparatorName) && Objects.equals(this.mergeOperatorName, tableProperties.mergeOperatorName) && Objects.equals(this.prefixExtractorName, tableProperties.prefixExtractorName) && Objects.equals(this.propertyCollectorsNames, tableProperties.propertyCollectorsNames) && Objects.equals(this.compressionName, tableProperties.compressionName) && Objects.equals(this.userCollectedProperties, tableProperties.userCollectedProperties) && Objects.equals(this.readableProperties, tableProperties.readableProperties);
    }

    public int hashCode() {
        int n = Objects.hash(this.dataSize, this.indexSize, this.indexPartitions, this.topLevelIndexSize, this.indexKeyIsUserKey, this.indexValueIsDeltaEncoded, this.filterSize, this.rawKeySize, this.rawValueSize, this.numDataBlocks, this.numEntries, this.numDeletions, this.numMergeOperands, this.numRangeDeletions, this.formatVersion, this.fixedKeyLen, this.columnFamilyId, this.creationTime, this.oldestKeyTime, this.slowCompressionEstimatedDataSize, this.fastCompressionEstimatedDataSize, this.externalSstFileGlobalSeqnoOffset, this.filterPolicyName, this.comparatorName, this.mergeOperatorName, this.prefixExtractorName, this.propertyCollectorsNames, this.compressionName, this.userCollectedProperties, this.readableProperties);
        n = 31 * n + Arrays.hashCode(this.columnFamilyName);
        return n;
    }
}

