package org.rocksdb;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractImmutableNativeReference extends AbstractNativeReference {
   protected final AtomicBoolean owningHandle_;

   protected AbstractImmutableNativeReference(boolean var1) {
      this.owningHandle_ = new AtomicBoolean(var1);
   }

   @Override
   public boolean isOwningHandle() {
      return this.owningHandle_.get();
   }

   protected final void disOwnNativeHandle() {
      this.owningHandle_.set(false);
   }

   @Override
   public void close() {
      if (this.owningHandle_.compareAndSet(true, false)) {
         this.disposeInternal();
      }
   }

   protected abstract void disposeInternal();
}
