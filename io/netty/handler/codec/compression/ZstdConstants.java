/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import com.github.luben.zstd.Zstd;

final class ZstdConstants {
    static final int DEFAULT_COMPRESSION_LEVEL = Zstd.defaultCompressionLevel();
    static final int MIN_COMPRESSION_LEVEL = Zstd.minCompressionLevel();
    static final int MAX_COMPRESSION_LEVEL = Zstd.maxCompressionLevel();
    static final int DEFAULT_MAX_ENCODE_SIZE = Integer.MAX_VALUE;
    static final int DEFAULT_BLOCK_SIZE = 65536;

    private ZstdConstants() {
    }
}

