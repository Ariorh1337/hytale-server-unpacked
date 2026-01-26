/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharBigList;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class CharMappedBigList
extends AbstractCharBigList {
    public static int LOG2_BYTES = 63 - Long.numberOfLeadingZeros(2L);
    @Deprecated
    public static int LOG2_BITS = 63 - Long.numberOfLeadingZeros(2L);
    private static int CHUNK_SHIFT = 30 - LOG2_BYTES;
    public static final long CHUNK_SIZE = 1L << CHUNK_SHIFT;
    private static final long CHUNK_MASK = CHUNK_SIZE - 1L;
    private final CharBuffer[] buffer;
    private final boolean[] readyToUse;
    private final int n;
    private final long size;

    protected CharMappedBigList(CharBuffer[] buffer, long size, boolean[] readyToUse) {
        this.buffer = buffer;
        this.n = buffer.length;
        this.size = size;
        this.readyToUse = readyToUse;
        for (int i = 0; i < this.n; ++i) {
            if (i >= this.n - 1 || (long)buffer[i].capacity() == CHUNK_SIZE) continue;
            throw new IllegalArgumentException();
        }
    }

    public static CharMappedBigList map(FileChannel fileChannel) throws IOException {
        return CharMappedBigList.map(fileChannel, ByteOrder.BIG_ENDIAN);
    }

    public static CharMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder) throws IOException {
        return CharMappedBigList.map(fileChannel, byteOrder, FileChannel.MapMode.READ_ONLY);
    }

    public static CharMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder, FileChannel.MapMode mapMode) throws IOException {
        long size = fileChannel.size() / 2L;
        int chunks = (int)((size + (CHUNK_SIZE - 1L)) / CHUNK_SIZE);
        CharBuffer[] buffer = new CharBuffer[chunks];
        for (int i = 0; i < chunks; ++i) {
            buffer[i] = fileChannel.map(mapMode, (long)i * CHUNK_SIZE * 2L, Math.min(CHUNK_SIZE, size - (long)i * CHUNK_SIZE) * 2L).order(byteOrder).asCharBuffer();
        }
        boolean[] readyToUse = new boolean[chunks];
        Arrays.fill(readyToUse, true);
        return new CharMappedBigList(buffer, size, readyToUse);
    }

    private CharBuffer CharBuffer(int n) {
        if (this.readyToUse[n]) {
            return this.buffer[n];
        }
        this.readyToUse[n] = true;
        this.buffer[n] = this.buffer[n].duplicate();
        return this.buffer[n];
    }

    public CharMappedBigList copy() {
        return new CharMappedBigList((CharBuffer[])this.buffer.clone(), this.size, new boolean[this.n]);
    }

    @Override
    public char getChar(long index) {
        return this.CharBuffer((int)(index >>> CHUNK_SHIFT)).get((int)(index & CHUNK_MASK));
    }

    @Override
    public void getElements(long from, char[] a, int offset, int length) {
        int chunk = (int)(from >>> CHUNK_SHIFT);
        int displ = (int)(from & CHUNK_MASK);
        while (length > 0) {
            CharBuffer b = this.CharBuffer(chunk);
            int l = Math.min(b.capacity() - displ, length);
            if (l == 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            b.position(displ);
            b.get(a, offset, l);
            if ((long)(displ += l) == CHUNK_SIZE) {
                displ = 0;
                ++chunk;
            }
            offset += l;
            length -= l;
        }
    }

    @Override
    public char set(long index, char value) {
        CharBuffer b = this.CharBuffer((int)(index >>> CHUNK_SHIFT));
        int i = (int)(index & CHUNK_MASK);
        char previousValue = b.get(i);
        b.put(i, value);
        return previousValue;
    }

    @Override
    public long size64() {
        return this.size;
    }
}

