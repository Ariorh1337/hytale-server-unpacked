package org.rocksdb;

public abstract class TablePropertiesCollectorFactory extends RocksObject {
   private TablePropertiesCollectorFactory(long var1) {
      super(var1);
   }

   public static TablePropertiesCollectorFactory NewCompactOnDeletionCollectorFactory(long var0, long var2, double var4) {
      long var6 = newCompactOnDeletionCollectorFactory(var0, var2, var4);
      return new TablePropertiesCollectorFactory(var6) {
         @Override
         protected void disposeInternal(long var1) {
            TablePropertiesCollectorFactory.deleteCompactOnDeletionCollectorFactory(var1);
         }
      };
   }

   static TablePropertiesCollectorFactory newWrapper(long var0) {
      return new TablePropertiesCollectorFactory(var0) {
         @Override
         protected void disposeInternal(long var1) {
            TablePropertiesCollectorFactory.deleteCompactOnDeletionCollectorFactory(var1);
         }
      };
   }

   private static native long newCompactOnDeletionCollectorFactory(long var0, long var2, double var4);

   private static native void deleteCompactOnDeletionCollectorFactory(long var0);
}
