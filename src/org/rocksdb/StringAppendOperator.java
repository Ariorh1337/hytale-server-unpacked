package org.rocksdb;

public class StringAppendOperator extends MergeOperator {
   public StringAppendOperator() {
      this(',');
   }

   public StringAppendOperator(char var1) {
      super(newSharedStringAppendOperator(var1));
   }

   public StringAppendOperator(String var1) {
      super(newSharedStringAppendOperator(var1));
   }

   private static native long newSharedStringAppendOperator(char var0);

   private static native long newSharedStringAppendOperator(String var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
