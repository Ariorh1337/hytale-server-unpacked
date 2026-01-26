/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.shop.barter;

import com.hypixel.hytale.builtin.adventure.shop.barter.BarterTrade;
import com.hypixel.hytale.builtin.adventure.shop.barter.TradeSlot;
import com.hypixel.hytale.builtin.adventure.shop.barter.WeightedTrade;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public class PoolTradeSlot
extends TradeSlot {
    public static final BuilderCodec<PoolTradeSlot> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PoolTradeSlot.class, PoolTradeSlot::new).append(new KeyedCodec<Integer>("SlotCount", Codec.INTEGER), (slot, count) -> {
        slot.slotCount = count;
    }, slot -> slot.slotCount).addValidator(Validators.greaterThanOrEqual(1)).add()).append(new KeyedCodec<T[]>("Trades", new ArrayCodec<WeightedTrade>(WeightedTrade.CODEC, WeightedTrade[]::new)), (slot, trades) -> {
        slot.trades = trades;
    }, slot -> slot.trades).addValidator(Validators.nonNull()).add()).build();
    protected int slotCount = 1;
    protected WeightedTrade[] trades = WeightedTrade.EMPTY_ARRAY;

    public PoolTradeSlot(int slotCount, @Nonnull WeightedTrade[] trades) {
        this.slotCount = slotCount;
        this.trades = trades;
    }

    protected PoolTradeSlot() {
    }

    public int getPoolSlotCount() {
        return this.slotCount;
    }

    @Nonnull
    public WeightedTrade[] getTrades() {
        return this.trades;
    }

    @Override
    @Nonnull
    public List<BarterTrade> resolve(@Nonnull Random random) {
        ObjectArrayList<BarterTrade> result = new ObjectArrayList<BarterTrade>(this.slotCount);
        if (this.trades.length == 0) {
            return result;
        }
        ObjectArrayList<WeightedTrade> available = new ObjectArrayList<WeightedTrade>(this.trades.length);
        available.addAll((Collection<WeightedTrade>)Arrays.asList(this.trades));
        int toSelect = Math.min(this.slotCount, available.size());
        for (int i = 0; i < toSelect; ++i) {
            int selectedIndex = this.selectWeightedIndex(available, random);
            if (selectedIndex < 0) continue;
            WeightedTrade selected = available.remove(selectedIndex);
            result.add(selected.toBarterTrade(random));
        }
        return result;
    }

    @Override
    public int getSlotCount() {
        return this.slotCount;
    }

    private int selectWeightedIndex(@Nonnull List<WeightedTrade> trades, @Nonnull Random random) {
        if (trades.isEmpty()) {
            return -1;
        }
        double totalWeight = 0.0;
        for (WeightedTrade trade : trades) {
            totalWeight += trade.getWeight();
        }
        if (totalWeight <= 0.0) {
            return random.nextInt(trades.size());
        }
        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (int i = 0; i < trades.size(); ++i) {
            if (!(roll < (cumulative += trades.get(i).getWeight()))) continue;
            return i;
        }
        return trades.size() - 1;
    }

    @Nonnull
    public String toString() {
        return "PoolTradeSlot{slotCount=" + this.slotCount + ", trades=" + Arrays.toString(this.trades) + "}";
    }
}

