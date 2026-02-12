package org.rocksdb;

public class Snapshot extends RocksObject {
   Snapshot(long var1) {
      super(var1);
      this.disOwnNativeHandle();
   }

   public long getSequenceNumber() {
      return getSequenceNumber(this.nativeHandle_);
   }

   @Override
   protected final void disposeInternal(long var1) {
   }

   private static native long getSequenceNumber(long var0);
}
