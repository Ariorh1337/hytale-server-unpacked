package org.rocksdb;

public class ConfigOptions extends RocksObject {
   public ConfigOptions() {
      super(newConfigOptionsInstance());
   }

   public ConfigOptions setDelimiter(String var1) {
      setDelimiter(this.nativeHandle_, var1);
      return this;
   }

   public ConfigOptions setIgnoreUnknownOptions(boolean var1) {
      setIgnoreUnknownOptions(this.nativeHandle_, var1);
      return this;
   }

   public ConfigOptions setEnv(Env var1) {
      setEnv(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public ConfigOptions setInputStringsEscaped(boolean var1) {
      setInputStringsEscaped(this.nativeHandle_, var1);
      return this;
   }

   public ConfigOptions setSanityLevel(SanityLevel var1) {
      setSanityLevel(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static long newConfigOptionsInstance() {
      RocksDB.loadLibrary();
      return newConfigOptions();
   }

   private static native long newConfigOptions();

   private static native void setEnv(long var0, long var2);

   private static native void setDelimiter(long var0, String var2);

   private static native void setIgnoreUnknownOptions(long var0, boolean var2);

   private static native void setInputStringsEscaped(long var0, boolean var2);

   private static native void setSanityLevel(long var0, byte var2);
}
