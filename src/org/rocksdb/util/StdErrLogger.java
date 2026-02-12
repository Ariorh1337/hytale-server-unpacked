package org.rocksdb.util;

import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.LoggerType;
import org.rocksdb.RocksObject;

public class StdErrLogger extends RocksObject implements LoggerInterface {
   public StdErrLogger(InfoLogLevel var1) {
      this(var1, null);
   }

   public StdErrLogger(InfoLogLevel var1, String var2) {
      super(newStdErrLogger(var1.getValue(), var2));
   }

   @Override
   public void setInfoLogLevel(InfoLogLevel var1) {
      setInfoLogLevel(this.nativeHandle_, var1.getValue());
   }

   @Override
   public InfoLogLevel infoLogLevel() {
      return InfoLogLevel.getInfoLogLevel(infoLogLevel(this.nativeHandle_));
   }

   @Override
   public LoggerType getLoggerType() {
      return LoggerType.STDERR_IMPLEMENTATION;
   }

   private static native long newStdErrLogger(byte var0, String var1);

   private static native void setInfoLogLevel(long var0, byte var2);

   private static native byte infoLogLevel(long var0);

   @Override
   protected native void disposeInternal(long var1);
}
