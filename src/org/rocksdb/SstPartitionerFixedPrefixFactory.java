package org.rocksdb;

public class SstPartitionerFixedPrefixFactory extends SstPartitionerFactory {
   public SstPartitionerFixedPrefixFactory(long var1) {
      super(newSstPartitionerFixedPrefixFactory0(var1));
   }

   private static native long newSstPartitionerFixedPrefixFactory0(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
