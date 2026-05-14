package org.rocksdb;

public enum CompressionType {
   NO_COMPRESSION((byte)0, null, "kNoCompression"),
   SNAPPY_COMPRESSION((byte)1, "snappy", "kSnappyCompression"),
   ZLIB_COMPRESSION((byte)2, "z", "kZlibCompression"),
   BZLIB2_COMPRESSION((byte)3, "bzip2", "kBZip2Compression"),
   LZ4_COMPRESSION((byte)4, "lz4", "kLZ4Compression"),
   LZ4HC_COMPRESSION((byte)5, "lz4hc", "kLZ4HCCompression"),
   XPRESS_COMPRESSION((byte)6, "xpress", "kXpressCompression"),
   ZSTD_COMPRESSION((byte)7, "zstd", "kZSTD"),
   DISABLE_COMPRESSION_OPTION((byte)127, null, "kDisableCompressionOption");

   private final byte value_;
   private final String libraryName_;
   private final String internalName_;

   public static CompressionType getCompressionType(String var0) {
      if (var0 != null) {
         for (CompressionType var4 : values()) {
            if (var4.getLibraryName() != null && var4.getLibraryName().equals(var0)) {
               return var4;
            }
         }
      }

      return NO_COMPRESSION;
   }

   public static CompressionType getCompressionType(byte var0) {
      for (CompressionType var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for CompressionType.");
   }

   static CompressionType getFromInternal(String var0) {
      for (CompressionType var4 : values()) {
         if (var4.internalName_.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal internalName '" + var0 + " ' provided for CompressionType.");
   }

   public byte getValue() {
      return this.value_;
   }

   public String getLibraryName() {
      return this.libraryName_;
   }

   CompressionType(final byte nullxx, final String nullxxx, final String nullxxxx) {
      this.value_ = nullxx;
      this.libraryName_ = nullxxx;
      this.internalName_ = nullxxxx;
   }
}
