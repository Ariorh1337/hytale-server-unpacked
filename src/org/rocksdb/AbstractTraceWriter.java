package org.rocksdb;

public abstract class AbstractTraceWriter extends RocksCallbackObject implements TraceWriter {
   public AbstractTraceWriter() {
      super();
   }

   @Override
   protected long initializeNative(long... var1) {
      return this.createNewTraceWriter();
   }

   private short writeProxy(long var1) {
      try {
         this.write(new Slice(var1));
         return statusToShort(Status.Code.Ok, Status.SubCode.None);
      } catch (RocksDBException var4) {
         return statusToShort(var4.getStatus());
      }
   }

   private short closeWriterProxy() {
      try {
         this.closeWriter();
         return statusToShort(Status.Code.Ok, Status.SubCode.None);
      } catch (RocksDBException var2) {
         return statusToShort(var2.getStatus());
      }
   }

   private static short statusToShort(Status var0) {
      Status.Code var1 = var0 != null && var0.getCode() != null ? var0.getCode() : Status.Code.IOError;
      Status.SubCode var2 = var0 != null && var0.getSubCode() != null ? var0.getSubCode() : Status.SubCode.None;
      return statusToShort(var1, var2);
   }

   private static short statusToShort(Status.Code var0, Status.SubCode var1) {
      short var2 = (short)(var0.getValue() << 8);
      return (short)(var2 | var1.getValue());
   }

   private native long createNewTraceWriter();
}
