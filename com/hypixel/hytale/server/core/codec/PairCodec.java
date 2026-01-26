/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import it.unimi.dsi.fastutil.Pair;
import javax.annotation.Nonnull;

public class PairCodec {

    public static class IntegerStringPair {
        public static final BuilderCodec<IntegerStringPair> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IntegerStringPair.class, IntegerStringPair::new).append(new KeyedCodec<Integer>("Left", Codec.INTEGER), (pair, left) -> {
            pair.left = left;
        }, pair -> pair.left).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("Right", Codec.STRING), (pair, right) -> {
            pair.right = right;
        }, pair -> pair.right).addValidator(Validators.nonNull()).add()).build();
        private Integer left;
        private String right;

        public IntegerStringPair() {
        }

        public IntegerStringPair(Integer left, String right) {
            this.left = left;
            this.right = right;
        }

        @Nonnull
        public Pair<Integer, String> toPair() {
            return Pair.of(this.left, this.right);
        }

        @Nonnull
        public static IntegerStringPair fromPair(@Nonnull Pair<Integer, String> pair) {
            return new IntegerStringPair(pair.left(), pair.right());
        }

        public Integer getLeft() {
            return this.left;
        }

        public String getRight() {
            return this.right;
        }
    }

    public static class IntegerPair {
        public static final BuilderCodec<IntegerPair> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IntegerPair.class, IntegerPair::new).append(new KeyedCodec<Integer>("Left", Codec.INTEGER), (pair, left) -> {
            pair.left = left;
        }, pair -> pair.left).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Integer>("Right", Codec.INTEGER), (pair, right) -> {
            pair.right = right;
        }, pair -> pair.right).addValidator(Validators.nonNull()).add()).build();
        private Integer left;
        private Integer right;

        public IntegerPair() {
        }

        public IntegerPair(Integer left, Integer right) {
            this.left = left;
            this.right = right;
        }

        @Nonnull
        public Pair<Integer, Integer> toPair() {
            return Pair.of(this.left, this.right);
        }

        @Nonnull
        public static IntegerPair fromPair(@Nonnull Pair<Integer, Integer> pair) {
            return new IntegerPair(pair.left(), pair.right());
        }

        public Integer getLeft() {
            return this.left;
        }

        public Integer getRight() {
            return this.right;
        }
    }
}

