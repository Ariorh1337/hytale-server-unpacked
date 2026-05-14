package org.rocksdb;

import java.util.Objects;

public class FileOperationInfo {
   private final String path;
   private final long offset;
   private final long length;
   private final long startTimestamp;
   private final long duration;
   private final Status status;

   FileOperationInfo(String var1, long var2, long var4, long var6, long var8, Status var10) {
      this.path = var1;
      this.offset = var2;
      this.length = var4;
      this.startTimestamp = var6;
      this.duration = var8;
      this.status = var10;
   }

   public String getPath() {
      return this.path;
   }

   public long getOffset() {
      return this.offset;
   }

   public long getLength() {
      return this.length;
   }

   public long getStartTimestamp() {
      return this.startTimestamp;
   }

   public long getDuration() {
      return this.duration;
   }

   public Status getStatus() {
      return this.status;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FileOperationInfo var2 = (FileOperationInfo)var1;
         return this.offset == var2.offset
            && this.length == var2.length
            && this.startTimestamp == var2.startTimestamp
            && this.duration == var2.duration
            && Objects.equals(this.path, var2.path)
            && Objects.equals(this.status, var2.status);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.path, this.offset, this.length, this.startTimestamp, this.duration, this.status);
   }

   @Override
   public String toString() {
      return "FileOperationInfo{path='"
         + this.path
         + "', offset="
         + this.offset
         + ", length="
         + this.length
         + ", startTimestamp="
         + this.startTimestamp
         + ", duration="
         + this.duration
         + ", status="
         + this.status
         + "}";
   }
}
