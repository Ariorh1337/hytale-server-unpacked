/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section.palette;

import com.hypixel.hytale.common.util.BitUtil;
import com.hypixel.hytale.protocol.packets.world.PaletteType;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.AbstractByteSectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ByteSectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.EmptySectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import java.util.BitSet;
import javax.annotation.Nonnull;

public class HalfByteSectionPalette
extends AbstractByteSectionPalette {
    private static final int KEY_MASK = 15;
    public static final int MAX_SIZE = 16;

    public HalfByteSectionPalette() {
        super(new byte[16384]);
    }

    public HalfByteSectionPalette(Int2ByteMap externalToInternal, Byte2IntMap internalToExternal, BitSet internalIdSet, Byte2ShortMap internalIdCount, byte[] blocks) {
        super(externalToInternal, internalToExternal, internalIdSet, internalIdCount, blocks);
    }

    public HalfByteSectionPalette(@Nonnull int[] data, int[] unique, int count) {
        super(new byte[16384], data, unique, count);
    }

    @Override
    @Nonnull
    public PaletteType getPaletteType() {
        return PaletteType.HalfByte;
    }

    @Override
    protected void set0(int idx, byte b) {
        BitUtil.setNibble(this.blocks, idx, b);
    }

    @Override
    protected byte get0(int idx) {
        return BitUtil.getNibble(this.blocks, idx);
    }

    @Override
    public boolean shouldDemote() {
        return this.isSolid(0);
    }

    @Override
    @Nonnull
    public ISectionPalette demote() {
        return EmptySectionPalette.INSTANCE;
    }

    @Override
    @Nonnull
    public ByteSectionPalette promote() {
        return ByteSectionPalette.fromHalfBytePalette(this);
    }

    @Override
    protected boolean isValidInternalId(int internalId) {
        return (internalId & 0xF) == internalId;
    }

    @Override
    protected int unsignedInternalId(byte internalId) {
        return internalId & 0xF;
    }

    private static int sUnsignedInternalId(byte internalId) {
        return internalId & 0xF;
    }

    @Nonnull
    public static HalfByteSectionPalette fromBytePalette(@Nonnull ByteSectionPalette section) {
        byte newInternalId;
        if (section.count() > 16) {
            throw new IllegalStateException("Cannot demote byte palette to half byte palette. Too many blocks! Count: " + section.count());
        }
        HalfByteSectionPalette halfByteSection = new HalfByteSectionPalette();
        Byte2ByteOpenHashMap internalIdRemapping = new Byte2ByteOpenHashMap();
        halfByteSection.internalToExternal.clear();
        halfByteSection.externalToInternal.clear();
        halfByteSection.internalIdSet.clear();
        for (Byte2IntMap.Entry entry : section.internalToExternal.byte2IntEntrySet()) {
            byte oldInternalId = entry.getByteKey();
            int externalId = entry.getIntValue();
            newInternalId = (byte)halfByteSection.internalIdSet.nextClearBit(0);
            halfByteSection.internalIdSet.set(HalfByteSectionPalette.sUnsignedInternalId(newInternalId));
            internalIdRemapping.put(oldInternalId, newInternalId);
            halfByteSection.internalToExternal.put(newInternalId, externalId);
            halfByteSection.externalToInternal.put(externalId, newInternalId);
        }
        halfByteSection.internalIdCount.clear();
        for (Byte2ShortMap.Entry entry : section.internalIdCount.byte2ShortEntrySet()) {
            byte internalId = entry.getByteKey();
            short count = entry.getShortValue();
            newInternalId = internalIdRemapping.get(internalId);
            halfByteSection.internalIdCount.put(newInternalId, count);
        }
        for (int i = 0; i < section.blocks.length; ++i) {
            byte by = section.blocks[i];
            byte byteInternalId = internalIdRemapping.get(by);
            halfByteSection.set0(i, byteInternalId);
        }
        return halfByteSection;
    }
}

