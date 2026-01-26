/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.blocktick.procedure;

import com.hypixel.hytale.builtin.blocktick.procedure.BasicChanceBlockGrowthProcedure;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.RandomUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.config.TickProcedure;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class SplitChanceBlockGrowthProcedure
extends BasicChanceBlockGrowthProcedure {
    public static final BuilderCodec<SplitChanceBlockGrowthProcedure> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SplitChanceBlockGrowthProcedure.class, SplitChanceBlockGrowthProcedure::new, TickProcedure.BASE_CODEC).append(new KeyedCodec<BsonDocument>("NextIds", Codec.BSON_DOCUMENT), (proc, v, extraInfo) -> {
        proc.data = new String[v.size()];
        proc.chances = new int[proc.data.length];
        int i = 0;
        for (Map.Entry<String, BsonValue> entry : v.entrySet()) {
            proc.data[i] = entry.getKey();
            proc.chances[i] = Codec.INTEGER.decode(entry.getValue(), (ExtraInfo)extraInfo);
            proc.sumChances += proc.chances[i];
            ++i;
        }
    }, (proc, extraInfo) -> {
        if (proc.data == null || proc.chances == null) {
            return null;
        }
        BsonDocument document = new BsonDocument();
        for (int i = 0; i < proc.data.length; ++i) {
            document.append(proc.data[i], Codec.INTEGER.encode(proc.chances[i], (ExtraInfo)extraInfo));
        }
        return document;
    }).addValidator(Validators.nonNull()).add()).addField(new KeyedCodec<Integer>("ChanceMin", Codec.INTEGER), (proc, v) -> {
        proc.chanceMin = v;
    }, proc -> proc.chanceMin)).addField(new KeyedCodec<Integer>("Data", Codec.INTEGER), (proc, v) -> {
        proc.chance = v;
    }, proc -> proc.chance)).addField(new KeyedCodec<Boolean>("NextTicking", Codec.BOOLEAN), (proc, v) -> {
        proc.nextTicking = v;
    }, proc -> proc.nextTicking)).build();
    protected int[] chances;
    protected String[] data;
    protected int sumChances;

    public SplitChanceBlockGrowthProcedure() {
    }

    public SplitChanceBlockGrowthProcedure(int chanceMin, int chance, @Nonnull int[] chances, @Nonnull String[] data, boolean nextTicking) {
        super(chanceMin, chance, null, nextTicking);
        this.chances = chances;
        this.data = data;
        if (chances.length != data.length) {
            throw new IllegalArgumentException(String.valueOf(data.length));
        }
        int localSumChances = 0;
        for (int c : chances) {
            if (c < 0) {
                throw new IllegalArgumentException(String.valueOf(c));
            }
            localSumChances += c;
        }
        this.sumChances = localSumChances;
    }

    @Override
    protected boolean executeToBlock(@Nonnull World world, int worldX, int worldY, int worldZ, String to) {
        String block = RandomUtil.roll(this.getRandom().nextInt(this.sumChances), this.data, this.chances);
        return super.executeToBlock(world, worldX, worldY, worldZ, block);
    }

    @Override
    @Nonnull
    public String toString() {
        return "SplitChanceBlockGrowthProcedure{chanceMin=" + this.chanceMin + ", chance=" + this.chance + ", to=" + this.to + ", nextTicking=" + this.nextTicking + ", chances=" + Arrays.toString(this.chances) + ", data=" + Arrays.toString(this.data) + ", sumChances=" + this.sumChances + "}";
    }
}

