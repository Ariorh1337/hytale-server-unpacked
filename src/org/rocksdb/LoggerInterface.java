package org.rocksdb;

public interface LoggerInterface {
   void setInfoLogLevel(InfoLogLevel var1);

   InfoLogLevel infoLogLevel();

   long getNativeHandle();

   LoggerType getLoggerType();
}
