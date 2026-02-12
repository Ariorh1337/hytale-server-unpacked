package org.rocksdb;

public class IngestExternalFileOptions extends RocksObject {
   public IngestExternalFileOptions() {
      super(newIngestExternalFileOptions());
   }

   public IngestExternalFileOptions(boolean var1, boolean var2, boolean var3, boolean var4) {
      super(newIngestExternalFileOptions(var1, var2, var3, var4));
   }

   public boolean moveFiles() {
      return moveFiles(this.nativeHandle_);
   }

   public IngestExternalFileOptions setMoveFiles(boolean var1) {
      setMoveFiles(this.nativeHandle_, var1);
      return this;
   }

   public boolean snapshotConsistency() {
      return snapshotConsistency(this.nativeHandle_);
   }

   public IngestExternalFileOptions setSnapshotConsistency(boolean var1) {
      setSnapshotConsistency(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowGlobalSeqNo() {
      return allowGlobalSeqNo(this.nativeHandle_);
   }

   public IngestExternalFileOptions setAllowGlobalSeqNo(boolean var1) {
      setAllowGlobalSeqNo(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowBlockingFlush() {
      return allowBlockingFlush(this.nativeHandle_);
   }

   public IngestExternalFileOptions setAllowBlockingFlush(boolean var1) {
      setAllowBlockingFlush(this.nativeHandle_, var1);
      return this;
   }

   public boolean ingestBehind() {
      return ingestBehind(this.nativeHandle_);
   }

   public IngestExternalFileOptions setIngestBehind(boolean var1) {
      setIngestBehind(this.nativeHandle_, var1);
      return this;
   }

   public boolean writeGlobalSeqno() {
      return writeGlobalSeqno(this.nativeHandle_);
   }

   public IngestExternalFileOptions setWriteGlobalSeqno(boolean var1) {
      setWriteGlobalSeqno(this.nativeHandle_, var1);
      return this;
   }

   private static native long newIngestExternalFileOptions();

   private static native long newIngestExternalFileOptions(boolean var0, boolean var1, boolean var2, boolean var3);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native boolean moveFiles(long var0);

   private static native void setMoveFiles(long var0, boolean var2);

   private static native boolean snapshotConsistency(long var0);

   private static native void setSnapshotConsistency(long var0, boolean var2);

   private static native boolean allowGlobalSeqNo(long var0);

   private static native void setAllowGlobalSeqNo(long var0, boolean var2);

   private static native boolean allowBlockingFlush(long var0);

   private static native void setAllowBlockingFlush(long var0, boolean var2);

   private static native boolean ingestBehind(long var0);

   private static native void setIngestBehind(long var0, boolean var2);

   private static native boolean writeGlobalSeqno(long var0);

   private static native void setWriteGlobalSeqno(long var0, boolean var2);
}
