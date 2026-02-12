package org.rocksdb;

public class LogFile {
   private final String pathName;
   private final long logNumber;
   private final WalFileType type;
   private final long startSequence;
   private final long sizeFileBytes;

   private LogFile(String var1, long var2, byte var4, long var5, long var7) {
      this.pathName = var1;
      this.logNumber = var2;
      this.type = WalFileType.fromValue(var4);
      this.startSequence = var5;
      this.sizeFileBytes = var7;
   }

   public String pathName() {
      return this.pathName;
   }

   public long logNumber() {
      return this.logNumber;
   }

   public WalFileType type() {
      return this.type;
   }

   public long startSequence() {
      return this.startSequence;
   }

   public long sizeFileBytes() {
      return this.sizeFileBytes;
   }
}
