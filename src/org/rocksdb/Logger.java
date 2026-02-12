package org.rocksdb;

public abstract class Logger extends RocksCallbackObject implements LoggerInterface {
   @Deprecated
   public Logger(Options var1) {
      this(var1.infoLogLevel());
   }

   @Deprecated
   public Logger(DBOptions var1) {
      this(var1.infoLogLevel());
   }

   public Logger(InfoLogLevel var1) {
      super(var1.getValue());
   }

   @Override
   protected long initializeNative(long... var1) {
      if (var1.length == 1) {
         return this.newLogger(var1[0]);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void setInfoLogLevel(InfoLogLevel var1) {
      this.setInfoLogLevel(this.nativeHandle_, var1.getValue());
   }

   @Override
   public InfoLogLevel infoLogLevel() {
      return InfoLogLevel.getInfoLogLevel(this.infoLogLevel(this.nativeHandle_));
   }

   @Override
   public long getNativeHandle() {
      return this.nativeHandle_;
   }

   @Override
   public final LoggerType getLoggerType() {
      return LoggerType.JAVA_IMPLEMENTATION;
   }

   protected abstract void log(InfoLogLevel var1, String var2);

   protected native long newLogger(long var1);

   protected native void setInfoLogLevel(long var1, byte var3);

   protected native byte infoLogLevel(long var1);

   @Override
   protected void disposeInternal() {
      this.disposeInternal(this.nativeHandle_);
   }

   private native void disposeInternal(long var1);
}
