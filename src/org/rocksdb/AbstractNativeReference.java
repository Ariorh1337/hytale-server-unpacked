package org.rocksdb;

public abstract class AbstractNativeReference implements AutoCloseable {
   protected abstract boolean isOwningHandle();

   @Override
   public abstract void close();
}
