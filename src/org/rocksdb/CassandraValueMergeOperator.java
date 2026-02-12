package org.rocksdb;

public class CassandraValueMergeOperator extends MergeOperator {
   public CassandraValueMergeOperator(int var1) {
      super(newSharedCassandraValueMergeOperator(var1, 0));
   }

   public CassandraValueMergeOperator(int var1, int var2) {
      super(newSharedCassandraValueMergeOperator(var1, var2));
   }

   private static native long newSharedCassandraValueMergeOperator(int var0, int var1);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
