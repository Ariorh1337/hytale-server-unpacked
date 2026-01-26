/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.ZstdCompressCtx;
import com.github.luben.zstd.ZstdDecompressCtx;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdException;
import com.github.luben.zstd.ZstdIOException;
import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Zstd {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compress(byte[] byArray, byte[] byArray2, int n, boolean bl) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n);
            zstdCompressCtx.setChecksum(bl);
            long l = zstdCompressCtx.compress(byArray, byArray2);
            return l;
        }
    }

    public static long compress(byte[] byArray, byte[] byArray2, int n) {
        return Zstd.compress(byArray, byArray2, n, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressByteArray(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4, int n5, boolean bl) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n5);
            zstdCompressCtx.setChecksum(bl);
            long l = zstdCompressCtx.compressByteArray(byArray, n, n2, byArray2, n3, n4);
            return l;
        }
    }

    public static long compressByteArray(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4, int n5) {
        return Zstd.compressByteArray(byArray, n, n2, byArray2, n3, n4, n5, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressDirectByteBuffer(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, int n5, boolean bl) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n5);
            zstdCompressCtx.setChecksum(bl);
            long l = zstdCompressCtx.compressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    public static long compressDirectByteBuffer(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, int n5) {
        return Zstd.compressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4, n5, false);
    }

    public static native long compressUnsafe(long var0, long var2, long var4, long var6, int var8, boolean var9);

    public static long compressUnsafe(long l, long l2, long l3, long l4, int n) {
        return Zstd.compressUnsafe(l, l2, l3, l4, n, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressUsingDict(byte[] byArray, int n, byte[] byArray2, int n2, int n3, byte[] byArray3, int n4) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n4);
            zstdCompressCtx.loadDict(byArray3);
            long l = zstdCompressCtx.compressByteArray(byArray, n, byArray.length - n, byArray2, n2, n3);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressUsingDict(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n3);
            zstdCompressCtx.loadDict(byArray3);
            long l = zstdCompressCtx.compressByteArray(byArray, n, byArray.length - n, byArray2, n2, byArray2.length - n2);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressDirectByteBufferUsingDict(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, byte[] byArray, int n5) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n5);
            zstdCompressCtx.loadDict(byArray);
            long l = zstdCompressCtx.compressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressFastDict(byte[] byArray, int n, byte[] byArray2, int n2, int n3, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            long l = zstdCompressCtx.compressByteArray(byArray, n, byArray.length - n, byArray2, n2, n3);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressFastDict(byte[] byArray, int n, byte[] byArray2, int n2, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            long l = zstdCompressCtx.compressByteArray(byArray, n, byArray.length - n, byArray2, n2, byArray2.length - n2);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compress(byte[] byArray, byte[] byArray2, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            long l = zstdCompressCtx.compress(byArray, byArray2);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long compressDirectByteBufferFastDict(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            long l = zstdCompressCtx.compressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompress(byte[] byArray, byte[] byArray2) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            long l = zstdDecompressCtx.decompress(byArray, byArray2);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decompress(byte[] byArray, ByteBuffer byteBuffer) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            int n = zstdDecompressCtx.decompress(byArray, byteBuffer);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressByteArray(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            long l = zstdDecompressCtx.decompressByteArray(byArray, n, n2, byArray2, n3, n4);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressDirectByteBuffer(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            long l = zstdDecompressCtx.decompressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    public static native long decompressUnsafe(long var0, long var2, long var4, long var6);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressUsingDict(byte[] byArray, int n, byte[] byArray2, int n2, int n3, byte[] byArray3) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(byArray3);
            long l = zstdDecompressCtx.decompressByteArray(byArray, n, byArray.length - n, byArray2, n2, n3);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressDirectByteBufferUsingDict(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, byte[] byArray) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(byArray);
            long l = zstdDecompressCtx.decompressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressFastDict(byte[] byArray, int n, byte[] byArray2, int n2, int n3, ZstdDictDecompress zstdDictDecompress) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(zstdDictDecompress);
            long l = zstdDecompressCtx.decompressByteArray(byArray, n, byArray.length - n, byArray2, n2, n3);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long decompressDirectByteBufferFastDict(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, ZstdDictDecompress zstdDictDecompress) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(zstdDictDecompress);
            long l = zstdDecompressCtx.decompressDirectByteBuffer(byteBuffer, n, n2, byteBuffer2, n3, n4);
            return l;
        }
    }

    public static native int loadDictDecompress(long var0, byte[] var2, int var3);

    public static native int loadFastDictDecompress(long var0, ZstdDictDecompress var2);

    public static native int loadDictCompress(long var0, byte[] var2, int var3);

    public static native int loadFastDictCompress(long var0, ZstdDictCompress var2);

    public static native void registerSequenceProducer(long var0, long var2, long var4);

    static native long getBuiltinSequenceProducer();

    static native void generateSequences(long var0, long var2, long var4, long var6, long var8);

    static native long getStubSequenceProducer();

    public static native int setCompressionChecksums(long var0, boolean var2);

    public static native int setCompressionMagicless(long var0, boolean var2);

    public static native int setCompressionLevel(long var0, int var2);

    public static native int setCompressionLong(long var0, int var2);

    public static native int setCompressionWorkers(long var0, int var2);

    public static native int setCompressionOverlapLog(long var0, int var2);

    public static native int setCompressionJobSize(long var0, int var2);

    public static native int setCompressionTargetLength(long var0, int var2);

    public static native int setCompressionMinMatch(long var0, int var2);

    public static native int setCompressionSearchLog(long var0, int var2);

    public static native int setCompressionChainLog(long var0, int var2);

    public static native int setCompressionHashLog(long var0, int var2);

    public static native int setCompressionWindowLog(long var0, int var2);

    public static native int setCompressionStrategy(long var0, int var2);

    public static native int setDecompressionLongMax(long var0, int var2);

    public static native int setDecompressionMagicless(long var0, boolean var2);

    public static native int setRefMultipleDDicts(long var0, boolean var2);

    public static native int setValidateSequences(long var0, int var2);

    public static native int setSequenceProducerFallback(long var0, boolean var2);

    public static native int setSearchForExternalRepcodes(long var0, int var2);

    public static native int setEnableLongDistanceMatching(long var0, int var2);

    public static long findFrameCompressedSize(byte[] byArray, int n, int n2) {
        if (n >= byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        if (n + n2 > byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n + n2);
        }
        long l = Zstd.findFrameCompressedSize0(byArray, n, n2);
        if (Zstd.isError(l)) {
            throw new ZstdException(l);
        }
        return l;
    }

    private static native long findFrameCompressedSize0(byte[] var0, int var1, int var2);

    public static long findFrameCompressedSize(byte[] byArray, int n) {
        return Zstd.findFrameCompressedSize(byArray, n, byArray.length - n);
    }

    public static long findFrameCompressedSize(byte[] byArray) {
        return Zstd.findFrameCompressedSize(byArray, 0);
    }

    public static long findFrameCompressedSize(ByteBuffer byteBuffer) {
        return Zstd.findDirectByteBufferFrameCompressedSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
    }

    public static native long findDirectByteBufferFrameCompressedSize(ByteBuffer var0, int var1, int var2);

    public static long getFrameContentSize(byte[] byArray, int n, int n2, boolean bl) {
        if (n >= byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        if (n + n2 > byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n + n2);
        }
        return Zstd.getFrameContentSize0(byArray, n, n2, bl);
    }

    private static native long getFrameContentSize0(byte[] var0, int var1, int var2, boolean var3);

    @Deprecated
    public static long decompressedSize(byte[] byArray, int n, int n2, boolean bl) {
        if (n >= byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        if (n + n2 > byArray.length) {
            throw new ArrayIndexOutOfBoundsException(n + n2);
        }
        return Zstd.decompressedSize0(byArray, n, n2, bl);
    }

    private static native long decompressedSize0(byte[] var0, int var1, int var2, boolean var3);

    public static long getFrameContentSize(byte[] byArray, int n, int n2) {
        return Zstd.getFrameContentSize(byArray, n, n2, false);
    }

    @Deprecated
    public static long decompressedSize(byte[] byArray, int n, int n2) {
        return Zstd.decompressedSize(byArray, n, n2, false);
    }

    public static long getFrameContentSize(byte[] byArray, int n) {
        return Zstd.getFrameContentSize(byArray, n, byArray.length - n);
    }

    @Deprecated
    public static long decompressedSize(byte[] byArray, int n) {
        return Zstd.decompressedSize(byArray, n, byArray.length - n);
    }

    public static long getFrameContentSize(byte[] byArray) {
        return Zstd.getFrameContentSize(byArray, 0);
    }

    @Deprecated
    public static long decompressedSize(byte[] byArray) {
        return Zstd.decompressedSize(byArray, 0);
    }

    @Deprecated
    public static native long decompressedDirectByteBufferSize(ByteBuffer var0, int var1, int var2, boolean var3);

    public static native long getDirectByteBufferFrameContentSize(ByteBuffer var0, int var1, int var2, boolean var3);

    @Deprecated
    public static long decompressedDirectByteBufferSize(ByteBuffer byteBuffer, int n, int n2) {
        return Zstd.decompressedDirectByteBufferSize(byteBuffer, n, n2, false);
    }

    public static long getDirectByteBufferFrameContentSize(ByteBuffer byteBuffer, int n, int n2) {
        return Zstd.getDirectByteBufferFrameContentSize(byteBuffer, n, n2, false);
    }

    public static native long compressBound(long var0);

    public static native boolean isError(long var0);

    public static native String getErrorName(long var0);

    public static native long getErrorCode(long var0);

    public static native long errNoError();

    public static native long errGeneric();

    public static native long errPrefixUnknown();

    public static native long errVersionUnsupported();

    public static native long errFrameParameterUnsupported();

    public static native long errFrameParameterWindowTooLarge();

    public static native long errCorruptionDetected();

    public static native long errChecksumWrong();

    public static native long errDictionaryCorrupted();

    public static native long errDictionaryWrong();

    public static native long errDictionaryCreationFailed();

    public static native long errParameterUnsupported();

    public static native long errParameterOutOfBound();

    public static native long errTableLogTooLarge();

    public static native long errMaxSymbolValueTooLarge();

    public static native long errMaxSymbolValueTooSmall();

    public static native long errStageWrong();

    public static native long errInitMissing();

    public static native long errMemoryAllocation();

    public static native long errWorkSpaceTooSmall();

    public static native long errDstSizeTooSmall();

    public static native long errSrcSizeWrong();

    public static native long errDstBufferNull();

    public static long trainFromBuffer(byte[][] byArray, byte[] byArray2, boolean bl) {
        return Zstd.trainFromBuffer(byArray, byArray2, bl, Zstd.defaultCompressionLevel());
    }

    public static long trainFromBuffer(byte[][] byArray, byte[] byArray2, boolean bl, int n) {
        if (byArray.length <= 10) {
            throw new ZstdException(Zstd.errGeneric(), "nb of samples too low");
        }
        return Zstd.trainFromBuffer0(byArray, byArray2, bl, n);
    }

    private static native long trainFromBuffer0(byte[][] var0, byte[] var1, boolean var2, int var3);

    public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] nArray, ByteBuffer byteBuffer2, boolean bl) {
        return Zstd.trainFromBufferDirect(byteBuffer, nArray, byteBuffer2, bl, Zstd.defaultCompressionLevel());
    }

    public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] nArray, ByteBuffer byteBuffer2, boolean bl, int n) {
        if (nArray.length <= 10) {
            throw new ZstdException(Zstd.errGeneric(), "nb of samples too low");
        }
        return Zstd.trainFromBufferDirect0(byteBuffer, nArray, byteBuffer2, bl, n);
    }

    private static native long trainFromBufferDirect0(ByteBuffer var0, int[] var1, ByteBuffer var2, boolean var3, int var4);

    public static native long getDictIdFromFrame(byte[] var0);

    public static native long getDictIdFromFrameBuffer(ByteBuffer var0);

    public static native long getDictIdFromDict(byte[] var0);

    private static native long getDictIdFromDictDirect(ByteBuffer var0, int var1, int var2);

    public static long getDictIdFromDictDirect(ByteBuffer byteBuffer) {
        int n = byteBuffer.limit() - byteBuffer.position();
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("dict must be a direct buffer");
        }
        if (n < 0) {
            throw new IllegalArgumentException("dict cannot be empty.");
        }
        return Zstd.getDictIdFromDictDirect(byteBuffer, byteBuffer.position(), n);
    }

    public static long trainFromBuffer(byte[][] byArray, byte[] byArray2) {
        return Zstd.trainFromBuffer(byArray, byArray2, false);
    }

    public static long trainFromBufferDirect(ByteBuffer byteBuffer, int[] nArray, ByteBuffer byteBuffer2) {
        return Zstd.trainFromBufferDirect(byteBuffer, nArray, byteBuffer2, false);
    }

    public static native int magicNumber();

    public static native int windowLogMin();

    public static native int windowLogMax();

    public static native int chainLogMin();

    public static native int chainLogMax();

    public static native int hashLogMin();

    public static native int hashLogMax();

    public static native int searchLogMin();

    public static native int searchLogMax();

    public static native int searchLengthMin();

    public static native int searchLengthMax();

    public static native int blockSizeMax();

    public static native int defaultCompressionLevel();

    public static native int minCompressionLevel();

    public static native int maxCompressionLevel();

    public static byte[] compress(byte[] byArray) {
        return Zstd.compress(byArray, Zstd.defaultCompressionLevel());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] compress(byte[] byArray, int n) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n);
            byte[] byArray2 = zstdCompressCtx.compress(byArray);
            return byArray2;
        }
    }

    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return Zstd.compress(byteBuffer, byteBuffer2, Zstd.defaultCompressionLevel());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int n, boolean bl) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n);
            zstdCompressCtx.setChecksum(bl);
            int n2 = zstdCompressCtx.compress(byteBuffer, byteBuffer2);
            return n2;
        }
    }

    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int n) {
        return Zstd.compress(byteBuffer, byteBuffer2, n, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer compress(ByteBuffer byteBuffer, int n) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.setLevel(n);
            ByteBuffer byteBuffer2 = zstdCompressCtx.compress(byteBuffer);
            return byteBuffer2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] compress(byte[] byArray, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            byte[] byArray2 = zstdCompressCtx.compress(byArray);
            return byArray2;
        }
    }

    @Deprecated
    public static long compressUsingDict(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        return Zstd.compressUsingDict(byArray, 0, byArray2, 0, byArray2.length, byArray3, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] compressUsingDict(byte[] byArray, byte[] byArray2, int n) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(byArray2);
            zstdCompressCtx.setLevel(n);
            byte[] byArray3 = zstdCompressCtx.compress(byArray);
            return byArray3;
        }
    }

    public static long compress(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        return Zstd.compressUsingDict(byArray, 0, byArray2, 0, byArray2.length, byArray3, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, byte[] byArray, int n) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(byArray);
            zstdCompressCtx.setLevel(n);
            int n2 = zstdCompressCtx.compress(byteBuffer, byteBuffer2);
            return n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer compress(ByteBuffer byteBuffer, byte[] byArray, int n) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(byArray);
            zstdCompressCtx.setLevel(n);
            ByteBuffer byteBuffer2 = zstdCompressCtx.compress(byteBuffer);
            return byteBuffer2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            int n = zstdCompressCtx.compress(byteBuffer, byteBuffer2);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer compress(ByteBuffer byteBuffer, ZstdDictCompress zstdDictCompress) {
        try (ZstdCompressCtx zstdCompressCtx = new ZstdCompressCtx();){
            zstdCompressCtx.loadDict(zstdDictCompress);
            zstdCompressCtx.setLevel(zstdDictCompress.level());
            ByteBuffer byteBuffer2 = zstdCompressCtx.compress(byteBuffer);
            return byteBuffer2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] byArray, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            byte[] byArray2 = zstdDecompressCtx.decompress(byArray, n);
            return byArray2;
        }
    }

    public static byte[] decompress(byte[] byArray) {
        ArrayList<FrameData> arrayList = new ArrayList<FrameData>();
        int n = Zstd.calculateContentSizeAndFrames(byArray, arrayList);
        byte[] byArray2 = new byte[n];
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < arrayList.size(); ++i) {
            FrameData frameData = (FrameData)arrayList.get(i);
            long l = Zstd.decompressByteArray(byArray2, n3, (int)frameData.contentSize, byArray, n2, (int)frameData.compressedSize);
            if (Zstd.isError(l)) {
                throw new ZstdException(l, String.format("error %s while decompressing %d frame", Zstd.getErrorName(l), i));
            }
            if (l != frameData.contentSize) {
                throw new IllegalStateException("decompressed size mismatch");
            }
            n2 += (int)frameData.compressedSize;
            n3 += (int)frameData.contentSize;
        }
        return byArray2;
    }

    private static int calculateContentSizeAndFrames(byte[] byArray, List<FrameData> list) {
        int n = 0;
        int n2 = 0;
        while (n2 < byArray.length) {
            FrameData frameData = new FrameData(byArray, n2);
            list.add(frameData);
            n2 += (int)frameData.compressedSize;
            n += (int)frameData.contentSize;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompressFrame(byte[] byArray, int n, int n2, int n3) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            byte[] byArray2 = zstdDecompressCtx.decompress(byArray, n, n2, n3);
            return byArray2;
        }
    }

    public static byte[] decompressFrame(byte[] byArray, int n) {
        int n2 = (int)Zstd.findFrameCompressedSize(byArray, n);
        long l = Zstd.getFrameContentSize(byArray, n, n2);
        if (Zstd.isError(l)) {
            if (l == -1L) {
                throw new ZstdException(l, "Content size is unknown");
            }
            throw new ZstdException(l);
        }
        return Zstd.decompressFrame(byArray, n, n2, (int)l);
    }

    public static byte[] decompressFrame(byte[] byArray) {
        return Zstd.decompressFrame(byArray, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            int n = zstdDecompressCtx.decompress(byteBuffer, byteBuffer2);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decompress(ByteBuffer byteBuffer, byte[] byArray) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            int n = zstdDecompressCtx.decompress(byteBuffer, byArray);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer decompress(ByteBuffer byteBuffer, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            ByteBuffer byteBuffer2 = zstdDecompressCtx.decompress(byteBuffer, n);
            return byteBuffer2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] byArray, ZstdDictDecompress zstdDictDecompress, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(zstdDictDecompress);
            byte[] byArray2 = zstdDecompressCtx.decompress(byArray, n);
            return byArray2;
        }
    }

    @Deprecated
    public static long decompressUsingDict(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        return Zstd.decompressUsingDict(byArray, 0, byArray2, 0, byArray2.length, byArray3);
    }

    public static long decompress(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        return Zstd.decompressUsingDict(byArray, 0, byArray2, 0, byArray2.length, byArray3);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] byArray, byte[] byArray2, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(byArray2);
            byte[] byArray3 = zstdDecompressCtx.decompress(byArray, n);
            return byArray3;
        }
    }

    @Deprecated
    public static long decompressedSize(ByteBuffer byteBuffer) {
        return Zstd.decompressedDirectByteBufferSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
    }

    public static long getFrameContentSize(ByteBuffer byteBuffer) {
        return Zstd.getDirectByteBufferFrameContentSize(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, byte[] byArray) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(byArray);
            int n = zstdDecompressCtx.decompress(byteBuffer, byteBuffer2);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer decompress(ByteBuffer byteBuffer, byte[] byArray, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(byArray);
            ByteBuffer byteBuffer2 = zstdDecompressCtx.decompress(byteBuffer, n);
            return byteBuffer2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ZstdDictDecompress zstdDictDecompress) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(zstdDictDecompress);
            int n = zstdDecompressCtx.decompress(byteBuffer, byteBuffer2);
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer decompress(ByteBuffer byteBuffer, ZstdDictDecompress zstdDictDecompress, int n) {
        try (ZstdDecompressCtx zstdDecompressCtx = new ZstdDecompressCtx();){
            zstdDecompressCtx.loadDict(zstdDictDecompress);
            ByteBuffer byteBuffer2 = zstdDecompressCtx.decompress(byteBuffer, n);
            return byteBuffer2;
        }
    }

    static ByteBuffer getArrayBackedBuffer(BufferPool bufferPool, int n) throws ZstdIOException {
        ByteBuffer byteBuffer = bufferPool.get(n);
        if (byteBuffer == null) {
            throw new ZstdIOException(Zstd.errMemoryAllocation(), "Cannot get ByteBuffer of size " + n + " from the BufferPool");
        }
        if (!byteBuffer.hasArray() || byteBuffer.arrayOffset() != 0) {
            bufferPool.release(byteBuffer);
            throw new IllegalArgumentException("provided ByteBuffer lacks array or has non-zero arrayOffset");
        }
        return byteBuffer;
    }

    static {
        Native.load();
    }

    private static class FrameData {
        final long contentSize;
        final long compressedSize;

        FrameData(byte[] byArray, int n) {
            this.compressedSize = Zstd.findFrameCompressedSize(byArray, n);
            this.contentSize = Zstd.getFrameContentSize(byArray, n, (int)this.compressedSize);
            if (Zstd.isError(this.contentSize)) {
                if (this.contentSize == -1L) {
                    throw new ZstdException(this.contentSize, "Content size is unknown");
                }
                throw new ZstdException(this.contentSize);
            }
        }
    }

    public static enum ParamSwitch {
        AUTO(0),
        ENABLE(1),
        DISABLE(2);

        private int val;

        private ParamSwitch(int n2) {
            this.val = n2;
        }

        public int getValue() {
            return this.val;
        }
    }
}

