package org.rocksdb;

public class UInt64AddOperator extends MergeOperator {
   public UInt64AddOperator() {
      super(newSharedUInt64AddOperator());
   }

   private static native long newSharedUInt64AddOperator();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
