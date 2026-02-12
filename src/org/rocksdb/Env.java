package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Env extends RocksObject {
   private static final AtomicReference<RocksEnv> SINGULAR_DEFAULT_ENV = new AtomicReference<>(null);

   public static Env getDefault() {
      RocksEnv var0;
      for (RocksEnv var1 = null; (var0 = SINGULAR_DEFAULT_ENV.get()) == null; SINGULAR_DEFAULT_ENV.compareAndSet(null, var1)) {
         if (var1 == null) {
            RocksDB.loadLibrary();
            var1 = new RocksEnv(getDefaultEnvInternal());
            var1.disOwnNativeHandle();
         }
      }

      return var0;
   }

   public Env setBackgroundThreads(int var1) {
      return this.setBackgroundThreads(var1, Priority.LOW);
   }

   public int getBackgroundThreads(Priority var1) {
      return getBackgroundThreads(this.nativeHandle_, var1.getValue());
   }

   public Env setBackgroundThreads(int var1, Priority var2) {
      setBackgroundThreads(this.nativeHandle_, var1, var2.getValue());
      return this;
   }

   public int getThreadPoolQueueLen(Priority var1) {
      return getThreadPoolQueueLen(this.nativeHandle_, var1.getValue());
   }

   public Env incBackgroundThreadsIfNeeded(int var1, Priority var2) {
      incBackgroundThreadsIfNeeded(this.nativeHandle_, var1, var2.getValue());
      return this;
   }

   public Env lowerThreadPoolIOPriority(Priority var1) {
      lowerThreadPoolIOPriority(this.nativeHandle_, var1.getValue());
      return this;
   }

   public Env lowerThreadPoolCPUPriority(Priority var1) {
      lowerThreadPoolCPUPriority(this.nativeHandle_, var1.getValue());
      return this;
   }

   public List<ThreadStatus> getThreadList() throws RocksDBException {
      return Arrays.asList(getThreadList(this.nativeHandle_));
   }

   Env(long var1) {
      super(var1);
   }

   private static native long getDefaultEnvInternal();

   private static native void setBackgroundThreads(long var0, int var2, byte var3);

   private static native int getBackgroundThreads(long var0, byte var2);

   private static native int getThreadPoolQueueLen(long var0, byte var2);

   private static native void incBackgroundThreadsIfNeeded(long var0, int var2, byte var3);

   private static native void lowerThreadPoolIOPriority(long var0, byte var2);

   private static native void lowerThreadPoolCPUPriority(long var0, byte var2);

   private static native ThreadStatus[] getThreadList(long var0) throws RocksDBException;
}
