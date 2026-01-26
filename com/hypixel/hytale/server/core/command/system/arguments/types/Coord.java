/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import javax.annotation.Nonnull;

public class Coord {
    private final double value;
    private final boolean height;
    private final boolean relative;
    private final boolean chunk;

    public Coord(double value, boolean height, boolean relative, boolean chunk) {
        this.value = value;
        this.height = height;
        this.relative = relative;
        this.chunk = chunk;
    }

    public double getValue() {
        return this.value;
    }

    public boolean isNotBase() {
        return !this.height && !this.relative && !this.chunk;
    }

    public boolean isHeight() {
        return this.height;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public boolean isChunk() {
        return this.chunk;
    }

    public double resolveXZ(double base) {
        return this.resolve(base);
    }

    public double resolveYAtWorldCoords(double base, @Nonnull World world, double x, double z) throws GeneralCommandException {
        if (this.height) {
            Object worldCoords = world.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(x, z));
            if (worldCoords == null) {
                throw new GeneralCommandException(Message.raw("Failed to load chunk at (" + x + ", " + z + ")"));
            }
            return (double)(((WorldChunk)worldCoords).getHeight(MathUtil.floor(x), MathUtil.floor(z)) + 1) + this.resolve(0.0);
        }
        return this.resolve(base);
    }

    protected double resolve(double base) {
        double val = this.chunk ? this.value * 32.0 : this.value;
        return this.relative ? val + base : val;
    }

    /*
     * Unable to fully structure code
     */
    @Nonnull
    public static Coord parse(@Nonnull String str) {
        height = false;
        relative = false;
        chunk = false;
        index = 0;
        block5: while (true) lbl-1000:
        // 3 sources

        {
            switch (str.charAt(index)) {
                case '_': {
                    height = true;
                    if (str.length() != ++index) ** GOTO lbl-1000
                    return new Coord(0.0, true, relative, chunk);
                }
                case '~': {
                    relative = true;
                    if (str.length() != ++index) continue block5;
                    return new Coord(0.0, height, true, chunk);
                }
                case 'c': {
                    chunk = true;
                    if (str.length() != ++index) break block5;
                    return new Coord(0.0, height, relative, true);
                }
            }
            break;
        }
        rest = str.substring(index);
        return new Coord(Double.parseDouble(rest), height, relative, chunk);
    }
}

