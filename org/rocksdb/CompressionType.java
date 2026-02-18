/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum CompressionType {
    NO_COMPRESSION(0, null, "kNoCompression"),
    SNAPPY_COMPRESSION(1, "snappy", "kSnappyCompression"),
    ZLIB_COMPRESSION(2, "z", "kZlibCompression"),
    BZLIB2_COMPRESSION(3, "bzip2", "kBZip2Compression"),
    LZ4_COMPRESSION(4, "lz4", "kLZ4Compression"),
    LZ4HC_COMPRESSION(5, "lz4hc", "kLZ4HCCompression"),
    XPRESS_COMPRESSION(6, "xpress", "kXpressCompression"),
    ZSTD_COMPRESSION(7, "zstd", "kZSTD"),
    DISABLE_COMPRESSION_OPTION(127, null, "kDisableCompressionOption");

    private final byte value_;
    private final String libraryName_;
    private final String internalName_;

    public static CompressionType getCompressionType(String string) {
        if (string != null) {
            for (CompressionType compressionType : CompressionType.values()) {
                if (compressionType.getLibraryName() == null || !compressionType.getLibraryName().equals(string)) continue;
                return compressionType;
            }
        }
        return NO_COMPRESSION;
    }

    public static CompressionType getCompressionType(byte by) {
        for (CompressionType compressionType : CompressionType.values()) {
            if (compressionType.getValue() != by) continue;
            return compressionType;
        }
        throw new IllegalArgumentException("Illegal value provided for CompressionType.");
    }

    static CompressionType getFromInternal(String string) {
        for (CompressionType compressionType : CompressionType.values()) {
            if (!compressionType.internalName_.equals(string)) continue;
            return compressionType;
        }
        throw new IllegalArgumentException("Illegal internalName '" + string + " ' provided for CompressionType.");
    }

    public byte getValue() {
        return this.value_;
    }

    public String getLibraryName() {
        return this.libraryName_;
    }

    private CompressionType(byte by, String string2, String string3) {
        this.value_ = by;
        this.libraryName_ = string2;
        this.internalName_ = string3;
    }
}

