/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ZstdDictTrainer {
    private final int allocatedSize;
    private final ByteBuffer trainingSamples;
    private final List<Integer> sampleSizes;
    private final int dictSize;
    private long filledSize;
    private int level;

    public ZstdDictTrainer(int n, int n2) {
        this(n, n2, Zstd.defaultCompressionLevel());
    }

    public ZstdDictTrainer(int n, int n2, int n3) {
        this.trainingSamples = ByteBuffer.allocateDirect(n);
        this.sampleSizes = new ArrayList<Integer>();
        this.allocatedSize = n;
        this.dictSize = n2;
        this.level = n3;
    }

    public synchronized boolean addSample(byte[] byArray) {
        if (this.filledSize + (long)byArray.length > (long)this.allocatedSize) {
            return false;
        }
        this.trainingSamples.put(byArray);
        this.sampleSizes.add(byArray.length);
        this.filledSize += (long)byArray.length;
        return true;
    }

    public ByteBuffer trainSamplesDirect() throws ZstdException {
        return this.trainSamplesDirect(false);
    }

    public synchronized ByteBuffer trainSamplesDirect(boolean bl) throws ZstdException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.dictSize);
        long l = Zstd.trainFromBufferDirect(this.trainingSamples, this.copyToIntArray(this.sampleSizes), byteBuffer, bl, this.level);
        if (Zstd.isError(l)) {
            byteBuffer.limit(0);
            throw new ZstdException(l);
        }
        byteBuffer.limit(Long.valueOf(l).intValue());
        return byteBuffer;
    }

    public byte[] trainSamples() throws ZstdException {
        return this.trainSamples(false);
    }

    public byte[] trainSamples(boolean bl) throws ZstdException {
        ByteBuffer byteBuffer = this.trainSamplesDirect(bl);
        byte[] byArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byArray);
        return byArray;
    }

    private int[] copyToIntArray(List<Integer> list) {
        int[] nArray = new int[list.size()];
        int n = 0;
        for (Integer n2 : list) {
            nArray[n] = n2;
            ++n;
        }
        return nArray;
    }
}

