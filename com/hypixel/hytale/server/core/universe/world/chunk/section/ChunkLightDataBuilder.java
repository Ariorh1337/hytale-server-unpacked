/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkLightData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkLightDataBuilder
extends ChunkLightData {
    static boolean DEBUG = false;
    protected BitSet currentSegments;

    public ChunkLightDataBuilder(short changeId) {
        super(null, changeId);
    }

    public ChunkLightDataBuilder(@Nonnull ChunkLightData lightData, short changeId) {
        super(lightData.light != null ? lightData.light.copy() : null, changeId);
        if (lightData instanceof ChunkLightDataBuilder) {
            throw new IllegalArgumentException("ChunkLightDataBuilder light data isn't compacted so we can't read this cleanly atm");
        }
        if (this.light != null) {
            this.currentSegments = new BitSet();
            this.currentSegments.set(0);
            ChunkLightDataBuilder.findSegments(this.light, 0, this.currentSegments);
        }
    }

    protected static void findSegments(@Nonnull ByteBuf light, int position, @Nonnull BitSet currentSegments) {
        byte mask = light.getByte(position * 17);
        for (int i = 0; i < 8; ++i) {
            int val = light.getUnsignedShort(position * 17 + i * 2 + 1);
            if ((mask >> i & 1) != 1) continue;
            currentSegments.set(val);
            ChunkLightDataBuilder.findSegments(light, val, currentSegments);
        }
    }

    public void setBlockLight(int x, int y, int z, byte red, byte green, byte blue) {
        this.setBlockLight(ChunkUtil.indexBlock(x, y, z), red, green, blue);
    }

    public void setBlockLight(int index, byte red, byte green, byte blue) {
        byte sky = this.getLight(index, 3);
        this.setLightRaw(index, ChunkLightDataBuilder.combineLightValues(red, green, blue, sky));
    }

    public void setSkyLight(int x, int y, int z, byte light) {
        this.setSkyLight(ChunkUtil.indexBlock(x, y, z), light);
    }

    public void setSkyLight(int index, byte light) {
        this.setLight(index, 3, light);
    }

    public void setLight(int index, int channel, byte value) {
        if (channel < 0 || channel >= 4) {
            throw new IllegalArgumentException();
        }
        int current = this.getLightRaw(index);
        current &= ~(15 << channel * 4);
        this.setLightRaw(index, (short)(current |= (value & 0xF) << channel * 4));
    }

    public void setLightRaw(int index, short value) {
        if (index < 0 || index >= 32768) {
            throw new IllegalArgumentException("Index " + index + " is outside of the bounds!");
        }
        if (this.light == null) {
            this.light = Unpooled.buffer(2176);
        }
        if (this.currentSegments == null) {
            this.currentSegments = new BitSet();
            this.currentSegments.set(0);
        }
        ChunkLightDataBuilder.setTraverse(this.light, this.currentSegments, index, 0, 0, value);
    }

    @Nonnull
    public ChunkLightData build() {
        if (this.light == null) {
            return new ChunkLightData(null, this.changeId);
        }
        ByteBuf buffer = Unpooled.buffer(this.currentSegments.cardinality() * 17);
        buffer.writerIndex(17);
        this.serializeOctree(buffer, 0, 0);
        return new ChunkLightData(buffer, this.changeId);
    }

    private int serializeOctree(@Nonnull ByteBuf to, int position, int segmentIndex) {
        int toPosition = segmentIndex;
        byte mask = this.light.getByte(position * 17);
        to.setByte(toPosition * 17, mask);
        for (int i = 0; i < 8; ++i) {
            int val = this.light.getUnsignedShort(position * 17 + i * 2 + 1);
            if ((mask >> i & 1) == 1) {
                to.ensureWritable(17);
                int nextSegmentIndex = ++segmentIndex;
                to.writerIndex((nextSegmentIndex + 1) * 17);
                int from = val;
                val = nextSegmentIndex;
                segmentIndex = this.serializeOctree(to, from, nextSegmentIndex);
            }
            to.setShort(toPosition * 17 + i * 2 + 1, val);
        }
        return segmentIndex;
    }

    @Nullable
    private static Res setTraverse(@Nonnull ByteBuf local, @Nonnull BitSet currentSegments, int index, int pointer, int depth, short value) {
        int headerLocation = pointer * 17;
        byte i = local.getByte(headerLocation);
        int innerIndex = index >> 12 - depth & 7;
        int position = innerIndex * 2 + headerLocation + 1;
        short currentValue = local.getShort(position);
        try {
            if ((i >> innerIndex & 1) == 1) {
                int currentValueMasked = currentValue & 0xFFFF;
                if (depth == 12) {
                    throw new RuntimeException("Discovered branch at deepest point in octree! Mask " + i + " innerIndex " + innerIndex + " depth " + depth + " setValue " + value + " currentValue " + currentValue + " at index " + index + " pointer " + pointer);
                }
                if (ChunkLightDataBuilder.setTraverse(local, currentSegments, index, currentValueMasked, depth + 3, value) != null) {
                    currentSegments.clear(currentValueMasked);
                    local.setShort(position, value);
                    int mask = ~(1 << innerIndex);
                    i = (byte)(i & mask);
                    local.setByte(headerLocation, i);
                    if (i == 0) {
                        for (int j = 0; j < 8; ++j) {
                            short s = local.getShort(j * 2 + headerLocation + 1);
                            if (s == value) continue;
                            return null;
                        }
                        return Res.INSTANCE;
                    }
                }
            } else if (value != currentValue) {
                if (depth > 12) {
                    throw new IllegalStateException("Somehow have invalid octree state: " + ChunkLightDataBuilder.octreeToString(local) + " when setTraverse(" + index + ", " + pointer + ", " + depth + ", " + value + ");");
                }
                if (depth == 12) {
                    byte[] bytes = null;
                    if (DEBUG) {
                        bytes = new byte[17];
                        local.getBytes(headerLocation, bytes, 0, bytes.length);
                    }
                    local.setShort(position, value);
                    for (int j = 0; j < 8; ++j) {
                        short s = local.getShort(j * 2 + headerLocation + 1);
                        if (s == value) continue;
                        return null;
                    }
                    return DEBUG ? new Res(ByteBufUtil.hexDump(bytes)) : Res.INSTANCE;
                }
                i = (byte)(i | 1 << innerIndex);
                local.setByte(headerLocation, i);
                int newSegmentIndex = ChunkLightDataBuilder.growSegment(local, currentSegments, currentValue);
                local.setShort(position, newSegmentIndex);
                Res out = ChunkLightDataBuilder.setTraverse(local, currentSegments, index, newSegmentIndex, depth + 3, value);
                if (out != null) {
                    throw new RuntimeException("Created new segment that instantly collapsed with (" + index + ", " + pointer + ", " + depth + ", " + value + "): with currentValue mismatch " + currentValue + " res " + String.valueOf(out));
                }
                return null;
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to setTraverse(" + index + ", " + pointer + ", " + depth + ", " + value + ") with i " + i + ", innerIndex " + innerIndex + ", position " + position + ", currentValue " + currentValue, t);
        }
        return null;
    }

    protected static int growSegment(@Nonnull ByteBuf local, @Nonnull BitSet currentSegments, short val) {
        int newSegmentIndex = currentSegments.nextClearBit(0);
        currentSegments.set(newSegmentIndex);
        int currentCapacity = local.capacity();
        if (currentCapacity <= (newSegmentIndex + 1) * 17) {
            int newCap = currentCapacity + 1088;
            local.capacity(newCap);
        }
        local.setByte(newSegmentIndex * 17, 0);
        for (int j = 0; j < 8; ++j) {
            local.setShort(newSegmentIndex * 17 + j * 2 + 1, val);
        }
        return newSegmentIndex;
    }

    @Nonnull
    public String toStringOctree() {
        if (this.light == null) {
            return "NULL";
        }
        return ChunkLightDataBuilder.octreeToString(this.light);
    }

    @Nonnull
    public static String octreeToString(@Nonnull ByteBuf buffer) {
        StringBuffer out = new StringBuffer();
        try {
            ChunkLightDataBuilder.octreeToString(buffer, 0, out, 0);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed at " + String.valueOf(out), t);
        }
        return out.toString();
    }

    public static void octreeToString(@Nonnull ByteBuf buffer, int pointer, @Nonnull StringBuffer out, int recursion) {
        byte i = buffer.getByte(pointer * 17);
        for (int j = 0; j < 8; ++j) {
            int loc = pointer * 17 + j * 2 + 1;
            int s = buffer.getUnsignedShort(loc);
            out.append("\t".repeat(Math.max(0, recursion)));
            if ((i & 1 << j) != 0) {
                out.append("SUBTREE AT ").append(j).append('\n');
                ChunkLightDataBuilder.octreeToString(buffer, s, out, recursion + 1);
            } else {
                out.append("INDEX ").append(j).append(" VALUE: ").append(s);
            }
            if (j == 7) continue;
            out.append('\n');
        }
    }

    private static class Res {
        public static final Res INSTANCE = new Res(null);
        private final String segment;

        private Res(String segment) {
            this.segment = segment;
        }

        @Nonnull
        public String toString() {
            return "Res{segment='" + this.segment + "'}";
        }
    }
}

