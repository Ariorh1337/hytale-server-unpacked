/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.blocktick.procedure;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktick.config.TickProcedure;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import javax.annotation.Nonnull;

public class BasicChanceBlockGrowthProcedure
extends TickProcedure {
    public static final BuilderCodec<BasicChanceBlockGrowthProcedure> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BasicChanceBlockGrowthProcedure.class, BasicChanceBlockGrowthProcedure::new, TickProcedure.BASE_CODEC).addField(new KeyedCodec<String>("NextId", Codec.STRING), (proc, v) -> {
        proc.to = v;
    }, proc -> proc.to)).addField(new KeyedCodec<Integer>("ChanceMin", Codec.INTEGER), (proc, v) -> {
        proc.chanceMin = v;
    }, proc -> proc.chanceMin)).addField(new KeyedCodec<Integer>("Chance", Codec.INTEGER), (proc, v) -> {
        proc.chance = v;
    }, proc -> proc.chance)).addField(new KeyedCodec<Boolean>("NextTicking", Codec.BOOLEAN), (proc, v) -> {
        proc.nextTicking = v;
    }, proc -> proc.nextTicking)).build();
    protected int chanceMin;
    protected int chance;
    protected String to;
    protected boolean nextTicking;

    public BasicChanceBlockGrowthProcedure() {
    }

    public BasicChanceBlockGrowthProcedure(int chanceMin, int chance, String to, boolean nextTicking) {
        this.chanceMin = chanceMin;
        this.chance = chance;
        this.to = to;
        this.nextTicking = nextTicking;
    }

    @Override
    @Nonnull
    public BlockTickStrategy onTick(@Nonnull World world, WorldChunk wc, int worldX, int worldY, int worldZ, int blockId) {
        if (!this.runChance()) {
            return BlockTickStrategy.CONTINUE;
        }
        if (this.executeToBlock(world, worldX, worldY, worldZ, this.to)) {
            return BlockTickStrategy.CONTINUE;
        }
        return BlockTickStrategy.SLEEP;
    }

    protected boolean runChance() {
        return this.getRandom().nextInt(this.chance) < this.chanceMin;
    }

    protected boolean executeToBlock(@Nonnull World world, int worldX, int worldY, int worldZ, String to) {
        world.setBlock(worldX, worldY, worldZ, to);
        return !this.nextTicking;
    }

    @Nonnull
    public String toString() {
        return "BasicChanceBlockGrowthProcedure{chanceMin=" + this.chanceMin + ", chance=" + this.chance + ", to=" + this.to + ", nextTicking=" + this.nextTicking + "}";
    }
}

