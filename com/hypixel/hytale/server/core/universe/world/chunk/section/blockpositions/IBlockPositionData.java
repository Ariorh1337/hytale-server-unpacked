/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section.blockpositions;

import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;

public interface IBlockPositionData {
    public BlockSection getChunkSection();

    public int getBlockType();

    public int getX();

    public int getY();

    public int getZ();

    public double getXCentre();

    public double getYCentre();

    public double getZCentre();
}

