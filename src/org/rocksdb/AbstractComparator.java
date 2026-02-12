package org.rocksdb;

import java.nio.ByteBuffer;

public abstract class AbstractComparator extends RocksCallbackObject {
   AbstractComparator() {
      super();
   }

   protected AbstractComparator(ComparatorOptions var1) {
      super(var1.nativeHandle_);
   }

   @Override
   protected long initializeNative(long... var1) {
      return this.createNewComparator(var1[0]);
   }

   ComparatorType getComparatorType() {
      return ComparatorType.JAVA_COMPARATOR;
   }

   public abstract String name();

   public abstract int compare(ByteBuffer var1, ByteBuffer var2);

   public void findShortestSeparator(ByteBuffer var1, ByteBuffer var2) {
   }

   public void findShortSuccessor(ByteBuffer var1) {
   }

   public final boolean usingDirectBuffers() {
      return usingDirectBuffers(this.nativeHandle_);
   }

   private static native boolean usingDirectBuffers(long var0);

   private native long createNewComparator(long var1);
}
