/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.palette;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.palette.BitFieldArr;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;

public class ShortBytePalette {
    public static final int LENGTH = 1024;
    private short count = 1;
    private final Lock keysLock = new ReentrantLock();
    private short[] keys = new short[]{0};
    private final BitFieldArr array = new BitFieldArr(10, 1024);

    public ShortBytePalette() {
    }

    public ShortBytePalette(short aDefault) {
        this.keys = new short[]{aDefault};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean set(int x, int z, short key) {
        short id = this.contains(key);
        int index = ChunkUtil.indexColumn(x, z);
        if (id >= 1024) {
            this.optimize(index);
            id = this.contains(key);
        }
        if (id >= 0) {
            this.array.set(index, id);
        } else {
            this.keysLock.lock();
            try {
                short oldId = this.contains(key);
                if (oldId >= 1024) {
                    this.optimize(index);
                    oldId = this.contains(key);
                }
                if (oldId >= 0) {
                    this.array.set(index, oldId);
                } else {
                    short s = this.count;
                    this.count = (short)(s + 1);
                    short newId = s;
                    if (newId >= Short.MAX_VALUE) {
                        throw new IllegalArgumentException("Can't have more than 32767");
                    }
                    if (newId >= 1024) {
                        this.optimize(index);
                        short s2 = this.count;
                        this.count = (short)(s2 + 1);
                        newId = s2;
                    }
                    if (newId >= this.keys.length) {
                        short[] keys = new short[newId + 1];
                        System.arraycopy(this.keys, 0, keys, 0, this.keys.length);
                        this.keys = keys;
                    }
                    this.keys[newId] = key;
                    this.array.set(index, newId);
                }
            }
            finally {
                this.keysLock.unlock();
            }
        }
        return true;
    }

    public short get(int x, int z) {
        return this.keys[this.array.get(ChunkUtil.indexColumn(x, z))];
    }

    public short get(int index) {
        return this.keys[this.array.get(index)];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public short contains(short key) {
        this.keysLock.lock();
        try {
            for (short i = 0; i < this.keys.length; i = (short)((short)(i + 1))) {
                if (this.keys[i] != key) continue;
                short s = i;
                return s;
            }
        }
        finally {
            this.keysLock.unlock();
        }
        return -1;
    }

    public void optimize() {
        this.optimize(-1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void optimize(int index) {
        ShortBytePalette shortBytePalette = new ShortBytePalette(this.keys[this.array.get(0)]);
        for (int i = 0; i < this.array.getLength(); ++i) {
            if (i == index) continue;
            shortBytePalette.set(ChunkUtil.xFromColumn(i), ChunkUtil.zFromColumn(i), this.keys[this.array.get(i)]);
        }
        this.keysLock.lock();
        try {
            this.count = shortBytePalette.count;
            this.keys = shortBytePalette.keys;
            this.array.set(shortBytePalette.array.get());
        }
        finally {
            this.keysLock.unlock();
        }
    }

    public void serialize(@Nonnull ByteBuf dos) {
        this.keysLock.lock();
        try {
            dos.writeShortLE(this.count);
            for (int i = 0; i < this.count; ++i) {
                dos.writeShortLE(this.keys[i]);
            }
            byte[] bytes = this.array.get();
            dos.writeIntLE(bytes.length);
            dos.writeBytes(bytes);
        }
        finally {
            this.keysLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deserialize(@Nonnull ByteBuf buf) {
        this.keysLock.lock();
        try {
            this.count = buf.readShortLE();
            this.keys = new short[this.count];
            for (int i = 0; i < this.count; ++i) {
                this.keys[i] = buf.readShortLE();
            }
            int length = buf.readIntLE();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            this.array.set(bytes);
            if (this.count == 0) {
                this.count = 1;
                this.keys = new short[]{0};
            }
        }
        finally {
            this.keysLock.unlock();
        }
    }

    public byte[] serialize() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        this.serialize(buf);
        return ByteBufUtil.getBytesRelease(buf);
    }

    public void copyFrom(@Nonnull ShortBytePalette other) {
        this.keysLock.lock();
        try {
            this.count = other.count;
            System.arraycopy(other.keys, 0, this.keys, 0, this.keys.length);
            this.array.copyFrom(other.array);
        }
        finally {
            this.keysLock.unlock();
        }
    }
}

