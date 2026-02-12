package org.rocksdb;

public class CassandraCompactionFilter extends AbstractCompactionFilter<Slice> {
   public CassandraCompactionFilter(boolean var1, int var2) {
      super(createNewCassandraCompactionFilter0(var1, var2));
   }

   private static native long createNewCassandraCompactionFilter0(boolean var0, int var1);
}
