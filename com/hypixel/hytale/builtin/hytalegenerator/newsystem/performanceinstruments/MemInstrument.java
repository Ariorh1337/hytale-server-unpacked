/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments;

import javax.annotation.Nonnull;

public interface MemInstrument {
    public static final long BYTES_IN_MEGABYTES = 1000000L;
    public static final long INT_SIZE = 4L;
    public static final long DOUBLE_SIZE = 8L;
    public static final long BOOLEAN_SIZE = 1L;
    public static final long OBJECT_REFERENCE_SIZE = 8L;
    public static final long OBJECT_HEADER_SIZE = 16L;
    public static final long ARRAY_HEADER_SIZE = 16L;
    public static final long CLASS_OBJECT_SIZE = 128L;
    public static final long ARRAYLIST_OBJECT_SIZE = 24L;
    public static final long VECTOR3I_SIZE = 28L;
    public static final long VECTOR3D_SIZE = 40L;
    public static final long HASHMAP_ENTRY_SIZE = 32L;

    @Nonnull
    public Report getMemoryUsage();

    public record Report(long size_bytes) {
        public Report {
            assert (size_bytes >= 0L);
        }
    }
}

