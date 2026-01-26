/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aayushatharva.brotli4j.encoder.Encoder$Parameters
 */
package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder;
import io.netty.handler.codec.compression.BrotliMode;
import io.netty.handler.codec.compression.BrotliOptions;
import io.netty.handler.codec.compression.DeflateOptions;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.SnappyOptions;
import io.netty.handler.codec.compression.ZstdOptions;
import io.netty.util.internal.ObjectUtil;

public final class StandardCompressionOptions {
    private StandardCompressionOptions() {
    }

    public static BrotliOptions brotli() {
        return BrotliOptions.DEFAULT;
    }

    @Deprecated
    public static BrotliOptions brotli(Encoder.Parameters parameters) {
        return new BrotliOptions(parameters);
    }

    public static BrotliOptions brotli(int quality, int window, BrotliMode mode) {
        ObjectUtil.checkInRange(quality, 0, 11, "quality");
        ObjectUtil.checkInRange(window, 10, 24, "window");
        ObjectUtil.checkNotNull(mode, "mode");
        Encoder.Parameters parameters = new Encoder.Parameters().setQuality(quality).setWindow(window).setMode(mode.adapt());
        return new BrotliOptions(parameters);
    }

    public static ZstdOptions zstd() {
        return ZstdOptions.DEFAULT;
    }

    public static ZstdOptions zstd(int compressionLevel, int blockSize, int maxEncodeSize) {
        return new ZstdOptions(compressionLevel, blockSize, maxEncodeSize);
    }

    public static SnappyOptions snappy() {
        return new SnappyOptions();
    }

    public static GzipOptions gzip() {
        return GzipOptions.DEFAULT;
    }

    public static GzipOptions gzip(int compressionLevel, int windowBits, int memLevel) {
        return new GzipOptions(compressionLevel, windowBits, memLevel);
    }

    public static DeflateOptions deflate() {
        return DeflateOptions.DEFAULT;
    }

    public static DeflateOptions deflate(int compressionLevel, int windowBits, int memLevel) {
        return new DeflateOptions(compressionLevel, windowBits, memLevel);
    }
}

