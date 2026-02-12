package org.rocksdb;

public enum PrepopulateBlobCache {
   PREPOPULATE_BLOB_DISABLE((byte)0, "prepopulate_blob_disable", "kDisable"),
   PREPOPULATE_BLOB_FLUSH_ONLY((byte)1, "prepopulate_blob_flush_only", "kFlushOnly");

   private final byte value_;
   private final String libraryName_;
   private final String internalName_;

   public static PrepopulateBlobCache getPrepopulateBlobCache(String var0) {
      if (var0 != null) {
         for (PrepopulateBlobCache var4 : values()) {
            if (var4.getLibraryName() != null && var4.getLibraryName().equals(var0)) {
               return var4;
            }
         }
      }

      return PREPOPULATE_BLOB_DISABLE;
   }

   public static PrepopulateBlobCache getPrepopulateBlobCache(byte var0) {
      for (PrepopulateBlobCache var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for PrepopulateBlobCache.");
   }

   static PrepopulateBlobCache getFromInternal(String var0) {
      for (PrepopulateBlobCache var4 : values()) {
         if (var4.internalName_.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal internalName '" + var0 + " ' provided for PrepopulateBlobCache.");
   }

   public byte getValue() {
      return this.value_;
   }

   public String getLibraryName() {
      return this.libraryName_;
   }

   PrepopulateBlobCache(byte var3, String var4, String var5) {
      this.value_ = var3;
      this.libraryName_ = var4;
      this.internalName_ = var5;
   }
}
