/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.shop.barter;

import com.hypixel.hytale.builtin.adventure.shop.barter.BarterItemStack;
import com.hypixel.hytale.builtin.adventure.shop.barter.BarterTrade;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.map.IWeightedElement;
import java.util.Arrays;
import java.util.Random;
import javax.annotation.Nonnull;

public class WeightedTrade
implements IWeightedElement {
    public static final BuilderCodec<WeightedTrade> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WeightedTrade.class, WeightedTrade::new).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE), (wt, w) -> {
        wt.weight = w;
    }, wt -> wt.weight).add()).append(new KeyedCodec<BarterItemStack>("Output", BarterItemStack.CODEC), (wt, stack) -> {
        wt.output = stack;
    }, wt -> wt.output).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<T[]>("Input", new ArrayCodec<BarterItemStack>(BarterItemStack.CODEC, BarterItemStack[]::new)), (wt, stacks) -> {
        wt.input = stacks;
    }, wt -> wt.input).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<int[]>("Stock", Codec.INT_ARRAY), (wt, arr) -> {
        wt.stockRange = arr;
    }, wt -> wt.stockRange).add()).build();
    public static final WeightedTrade[] EMPTY_ARRAY = new WeightedTrade[0];
    protected double weight = 100.0;
    protected BarterItemStack output;
    protected BarterItemStack[] input;
    protected int[] stockRange = new int[]{10};

    public WeightedTrade(double weight, @Nonnull BarterItemStack output, @Nonnull BarterItemStack[] input, int stock) {
        this.weight = weight;
        this.output = output;
        this.input = input;
        this.stockRange = new int[]{stock};
    }

    public WeightedTrade(double weight, @Nonnull BarterItemStack output, @Nonnull BarterItemStack[] input, int stockMin, int stockMax) {
        this.weight = weight;
        this.output = output;
        this.input = input;
        this.stockRange = new int[]{stockMin, stockMax};
    }

    protected WeightedTrade() {
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Nonnull
    public BarterItemStack getOutput() {
        return this.output;
    }

    @Nonnull
    public BarterItemStack[] getInput() {
        return this.input;
    }

    @Nonnull
    public int[] getStockRange() {
        return this.stockRange;
    }

    public boolean hasStockRange() {
        return this.stockRange != null && this.stockRange.length == 2;
    }

    public int getStockMin() {
        return this.stockRange != null && this.stockRange.length > 0 ? this.stockRange[0] : 10;
    }

    public int getStockMax() {
        return this.stockRange != null && this.stockRange.length > 1 ? this.stockRange[1] : this.getStockMin();
    }

    public int resolveStock(@Nonnull Random random) {
        int max;
        if (!this.hasStockRange()) {
            return this.getStockMin();
        }
        int min = this.getStockMin();
        if (min >= (max = this.getStockMax())) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    @Nonnull
    public BarterTrade toBarterTrade(@Nonnull Random random) {
        return new BarterTrade(this.output, this.input, this.resolveStock(random));
    }

    @Nonnull
    public BarterTrade toBarterTrade() {
        return new BarterTrade(this.output, this.input, this.getStockMin());
    }

    @Nonnull
    public String toString() {
        String stockStr = this.hasStockRange() ? "[" + this.getStockMin() + ", " + this.getStockMax() + "]" : String.valueOf(this.getStockMin());
        return "WeightedTrade{weight=" + this.weight + ", output=" + String.valueOf(this.output) + ", input=" + Arrays.toString(this.input) + ", stock=" + stockStr + "}";
    }
}

