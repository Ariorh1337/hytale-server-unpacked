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

   TableProperties(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      long var15,
      long var17,
      long var19,
      long var21,
      long var23,
      long var25,
      long var27,
      long var29,
      long var31,
      long var33,
      long var35,
      long var37,
      long var39,
      long var41,
      long var43,
      byte[] var45,
      String var46,
      String var47,
      String var48,
      String var49,
      String var50,
      String var51,
      Map<String, String> var52,
      Map<String, String> var53
   ) {
      this.dataSize = var1;
      this.indexSize = var3;
      this.indexPartitions = var5;
      this.topLevelIndexSize = var7;
      this.indexKeyIsUserKey = var9;
      this.indexValueIsDeltaEncoded = var11;
      this.filterSize = var13;
      this.rawKeySize = var15;
      this.rawValueSize = var17;
      this.numDataBlocks = var19;
      this.numEntries = var21;
      this.numDeletions = var23;
      this.numMergeOperands = var25;
      this.numRangeDeletions = var27;
      this.formatVersion = var29;
      this.fixedKeyLen = var31;
      this.columnFamilyId = var33;
      this.creationTime = var35;
      this.oldestKeyTime = var37;
      this.slowCompressionEstimatedDataSize = var39;
      this.fastCompressionEstimatedDataSize = var41;
      this.externalSstFileGlobalSeqnoOffset = var43;
      this.columnFamilyName = var45;
      this.filterPolicyName = var46;
      this.comparatorName = var47;
      this.mergeOperatorName = var48;
      this.prefixExtractorName = var49;
      this.propertyCollectorsNames = var50;
      this.compressionName = var51;
      this.userCollectedProperties = var52;
      this.readableProperties = var53;
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

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         TableProperties var2 = (TableProperties)var1;
         return this.dataSize == var2.dataSize
            && this.indexSize == var2.indexSize
            && this.indexPartitions == var2.indexPartitions
            && this.topLevelIndexSize == var2.topLevelIndexSize
            && this.indexKeyIsUserKey == var2.indexKeyIsUserKey
            && this.indexValueIsDeltaEncoded == var2.indexValueIsDeltaEncoded
            && this.filterSize == var2.filterSize
            && this.rawKeySize == var2.rawKeySize
            && this.rawValueSize == var2.rawValueSize
            && this.numDataBlocks == var2.numDataBlocks
            && this.numEntries == var2.numEntries
            && this.numDeletions == var2.numDeletions
            && this.numMergeOperands == var2.numMergeOperands
            && this.numRangeDeletions == var2.numRangeDeletions
            && this.formatVersion == var2.formatVersion
            && this.fixedKeyLen == var2.fixedKeyLen
            && this.columnFamilyId == var2.columnFamilyId
            && this.creationTime == var2.creationTime
            && this.oldestKeyTime == var2.oldestKeyTime
            && this.slowCompressionEstimatedDataSize == var2.slowCompressionEstimatedDataSize
            && this.fastCompressionEstimatedDataSize == var2.fastCompressionEstimatedDataSize
            && this.externalSstFileGlobalSeqnoOffset == var2.externalSstFileGlobalSeqnoOffset
            && Arrays.equals(this.columnFamilyName, var2.columnFamilyName)
            && Objects.equals(this.filterPolicyName, var2.filterPolicyName)
            && Objects.equals(this.comparatorName, var2.comparatorName)
            && Objects.equals(this.mergeOperatorName, var2.mergeOperatorName)
            && Objects.equals(this.prefixExtractorName, var2.prefixExtractorName)
            && Objects.equals(this.propertyCollectorsNames, var2.propertyCollectorsNames)
            && Objects.equals(this.compressionName, var2.compressionName)
            && Objects.equals(this.userCollectedProperties, var2.userCollectedProperties)
            && Objects.equals(this.readableProperties, var2.readableProperties);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = Objects.hash(
         this.dataSize,
         this.indexSize,
         this.indexPartitions,
         this.topLevelIndexSize,
         this.indexKeyIsUserKey,
         this.indexValueIsDeltaEncoded,
         this.filterSize,
         this.rawKeySize,
         this.rawValueSize,
         this.numDataBlocks,
         this.numEntries,
         this.numDeletions,
         this.numMergeOperands,
         this.numRangeDeletions,
         this.formatVersion,
         this.fixedKeyLen,
         this.columnFamilyId,
         this.creationTime,
         this.oldestKeyTime,
         this.slowCompressionEstimatedDataSize,
         this.fastCompressionEstimatedDataSize,
         this.externalSstFileGlobalSeqnoOffset,
         this.filterPolicyName,
         this.comparatorName,
         this.mergeOperatorName,
         this.prefixExtractorName,
         this.propertyCollectorsNames,
         this.compressionName,
         this.userCollectedProperties,
         this.readableProperties
      );
      return 31 * var1 + Arrays.hashCode(this.columnFamilyName);
   }
}
