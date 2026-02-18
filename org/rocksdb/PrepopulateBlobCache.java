/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum PrepopulateBlobCache {
    PREPOPULATE_BLOB_DISABLE(0, "prepopulate_blob_disable", "kDisable"),
    PREPOPULATE_BLOB_FLUSH_ONLY(1, "prepopulate_blob_flush_only", "kFlushOnly");

    private final byte value_;
    private final String libraryName_;
    private final String internalName_;

    public static PrepopulateBlobCache getPrepopulateBlobCache(String string) {
        if (string != null) {
            for (PrepopulateBlobCache prepopulateBlobCache : PrepopulateBlobCache.values()) {
                if (prepopulateBlobCache.getLibraryName() == null || !prepopulateBlobCache.getLibraryName().equals(string)) continue;
                return prepopulateBlobCache;
            }
        }
        return PREPOPULATE_BLOB_DISABLE;
    }

    public static PrepopulateBlobCache getPrepopulateBlobCache(byte by) {
        for (PrepopulateBlobCache prepopulateBlobCache : PrepopulateBlobCache.values()) {
            if (prepopulateBlobCache.getValue() != by) continue;
            return prepopulateBlobCache;
        }
        throw new IllegalArgumentException("Illegal value provided for PrepopulateBlobCache.");
    }

    static PrepopulateBlobCache getFromInternal(String string) {
        for (PrepopulateBlobCache prepopulateBlobCache : PrepopulateBlobCache.values()) {
            if (!prepopulateBlobCache.internalName_.equals(string)) continue;
            return prepopulateBlobCache;
        }
        throw new IllegalArgumentException("Illegal internalName '" + string + " ' provided for PrepopulateBlobCache.");
    }

    public byte getValue() {
        return this.value_;
    }

    public String getLibraryName() {
        return this.libraryName_;
    }

    private PrepopulateBlobCache(byte by, String string2, String string3) {
        this.value_ = by;
        this.libraryName_ = string2;
        this.internalName_ = string3;
    }
}

