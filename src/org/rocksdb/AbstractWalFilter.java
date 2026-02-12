package org.rocksdb;

public abstract class AbstractWalFilter extends RocksCallbackObject implements WalFilter {
   public AbstractWalFilter() {
      super();
   }

   @Override
   protected long initializeNative(long... var1) {
      return this.createNewWalFilter();
   }

   private short logRecordFoundProxy(long var1, String var3, long var4, long var6) {
      WalFilter.LogRecordFoundResult var8 = this.logRecordFound(var1, var3, new WriteBatch(var4), new WriteBatch(var6));
      return logRecordFoundResultToShort(var8);
   }

   private static short logRecordFoundResultToShort(WalFilter.LogRecordFoundResult var0) {
      short var1 = (short)(var0.walProcessingOption.getValue() << 8);
      return (short)(var1 | (var0.batchChanged ? 1 : 0));
   }

   private native long createNewWalFilter();
}
