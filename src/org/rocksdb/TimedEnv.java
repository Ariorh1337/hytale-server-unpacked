package org.rocksdb;

public class TimedEnv extends Env {
   public TimedEnv(Env var1) {
      super(createTimedEnv(var1.nativeHandle_));
   }

   private static native long createTimedEnv(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
