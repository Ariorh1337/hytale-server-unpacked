package org.rocksdb;

public abstract class AbstractCompactionFilterFactory<T extends AbstractCompactionFilter<?>> extends RocksCallbackObject {
   public AbstractCompactionFilterFactory() {
      super(0L);
   }

   @Override
   protected long initializeNative(long... var1) {
      return this.createNewCompactionFilterFactory0();
   }

   private long createCompactionFilter(boolean var1, boolean var2) {
      AbstractCompactionFilter var3 = this.createCompactionFilter(new AbstractCompactionFilter.Context(var1, var2));
      var3.disOwnNativeHandle();
      return var3.nativeHandle_;
   }

   public abstract T createCompactionFilter(AbstractCompactionFilter.Context var1);

   public abstract String name();

   @Override
   protected void disposeInternal() {
      disposeInternal(this.nativeHandle_);
   }

   private native long createNewCompactionFilterFactory0();

   private static native void disposeInternal(long var0);
}
