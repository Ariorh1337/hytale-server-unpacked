package org.rocksdb;

import java.util.Arrays;
import java.util.Objects;

public class ColumnFamilyHandle extends RocksObject {
   private final RocksDB rocksDB_;

   ColumnFamilyHandle(RocksDB var1, long var2) {
      super(var2);
      assert var1 != null;
      this.rocksDB_ = var1;
   }

   ColumnFamilyHandle(long var1) {
      super(var1);
      this.rocksDB_ = null;
      this.disOwnNativeHandle();
   }

   public byte[] getName() throws RocksDBException {
      assert this.isOwningHandle() || this.isDefaultColumnFamily();
      return getName(this.nativeHandle_);
   }

   public int getID() {
      assert this.isOwningHandle() || this.isDefaultColumnFamily();
      return getID(this.nativeHandle_);
   }

   public ColumnFamilyDescriptor getDescriptor() throws RocksDBException {
      assert this.isOwningHandle() || this.isDefaultColumnFamily();
      return getDescriptor(this.nativeHandle_);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         ColumnFamilyHandle var2 = (ColumnFamilyHandle)var1;

         try {
            return this.rocksDB_.nativeHandle_ == var2.rocksDB_.nativeHandle_ && this.getID() == var2.getID() && Arrays.equals(this.getName(), var2.getName());
         } catch (RocksDBException var4) {
            throw new RuntimeException("Cannot compare column family handles", var4);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      try {
         int var1 = Objects.hash(this.getID(), this.rocksDB_.nativeHandle_);
         return 31 * var1 + Arrays.hashCode(this.getName());
      } catch (RocksDBException var2) {
         throw new RuntimeException("Cannot calculate hash code of column family handle", var2);
      }
   }

   protected boolean isDefaultColumnFamily() {
      return this.nativeHandle_ == this.rocksDB_.getDefaultColumnFamily().nativeHandle_;
   }

   @Override
   protected void disposeInternal() {
      if (this.rocksDB_.isOwningHandle()) {
         this.disposeInternal(this.nativeHandle_);
      }
   }

   private static native byte[] getName(long var0) throws RocksDBException;

   private static native int getID(long var0);

   private static native ColumnFamilyDescriptor getDescriptor(long var0) throws RocksDBException;

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
