/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
    private static final InternalLogger logger;
    private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
    private static final int DEFAULT_JDK_MEM_LEVEL = 8;
    private static final boolean noJdkZlibDecoder;
    private static final boolean noJdkZlibEncoder;
    private static final boolean JZLIB_AVAILABLE;

    public static boolean isSupportingWindowSizeAndMemLevel() {
        return JZLIB_AVAILABLE;
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel) {
        if (noJdkZlibEncoder) {
            return new JZlibEncoder(compressionLevel);
        }
        return new JdkZlibEncoder(compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
        if (noJdkZlibEncoder) {
            return new JZlibEncoder(wrapper);
        }
        return new JdkZlibEncoder(wrapper);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (noJdkZlibEncoder) {
            return new JZlibEncoder(wrapper, compressionLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (noJdkZlibEncoder || windowBits != 15 || memLevel != 8) {
            return new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
        if (noJdkZlibEncoder) {
            return new JZlibEncoder(dictionary);
        }
        return new JdkZlibEncoder(dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (noJdkZlibEncoder) {
            return new JZlibEncoder(compressionLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (noJdkZlibEncoder || windowBits != 15 || memLevel != 8) {
            return new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    @Deprecated
    public static ZlibDecoder newZlibDecoder() {
        return ZlibCodecFactory.newZlibDecoder(0);
    }

    public static ZlibDecoder newZlibDecoder(int maxAllocation) {
        if (noJdkZlibDecoder) {
            return new JZlibDecoder(maxAllocation);
        }
        return new JdkZlibDecoder(true, maxAllocation);
    }

    @Deprecated
    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
        return ZlibCodecFactory.newZlibDecoder(wrapper, 0);
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
        if (noJdkZlibDecoder) {
            return new JZlibDecoder(wrapper, maxAllocation);
        }
        return new JdkZlibDecoder(wrapper, true, maxAllocation);
    }

    @Deprecated
    public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
        return ZlibCodecFactory.newZlibDecoder(dictionary, 0);
    }

    public static ZlibDecoder newZlibDecoder(byte[] dictionary, int maxAllocation) {
        if (noJdkZlibDecoder) {
            return new JZlibDecoder(dictionary, maxAllocation);
        }
        return new JdkZlibDecoder(dictionary, maxAllocation);
    }

    private ZlibCodecFactory() {
    }

    static {
        boolean jzlibAvailable;
        logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
        noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", false);
        logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object)noJdkZlibDecoder);
        noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
        logger.debug("-Dio.netty.noJdkZlibEncoder: {}", (Object)noJdkZlibEncoder);
        try {
            Class.forName("com.jcraft.jzlib.JZlib", false, PlatformDependent.getClassLoader(ZlibCodecFactory.class));
            jzlibAvailable = true;
        }
        catch (ClassNotFoundException t) {
            jzlibAvailable = false;
            logger.debug("JZlib not in the classpath; the only window bits supported value will be 15");
        }
        JZLIB_AVAILABLE = jzlibAvailable;
    }
}

