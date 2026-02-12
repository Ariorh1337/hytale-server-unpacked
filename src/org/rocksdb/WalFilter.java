package org.rocksdb;

import java.util.Map;

public interface WalFilter {
   void columnFamilyLogNumberMap(Map<Integer, Long> var1, Map<String, Integer> var2);

   WalFilter.LogRecordFoundResult logRecordFound(long var1, String var3, WriteBatch var4, WriteBatch var5);

   String name();

   class LogRecordFoundResult {
      public static WalFilter.LogRecordFoundResult CONTINUE_UNCHANGED = new WalFilter.LogRecordFoundResult(WalProcessingOption.CONTINUE_PROCESSING, false);
      final WalProcessingOption walProcessingOption;
      final boolean batchChanged;

      public LogRecordFoundResult(WalProcessingOption var1, boolean var2) {
         this.walProcessingOption = var1;
         this.batchChanged = var2;
      }
   }
}
