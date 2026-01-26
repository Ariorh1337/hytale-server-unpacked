/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.tagpattern.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.TagPattern;
import java.util.Arrays;
import javax.annotation.Nonnull;

public abstract class MultiplePatternOp
extends TagPattern {
    @Nonnull
    public static BuilderCodec<MultiplePatternOp> CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(MultiplePatternOp.class, TagPattern.BASE_CODEC).append(new KeyedCodec<T[]>("Patterns", new ArrayCodec(TagPattern.CODEC, TagPattern[]::new)), (tagPattern, tagPatterns) -> {
        tagPattern.patterns = tagPatterns;
    }, tagPattern -> tagPattern.patterns).addValidator(Validators.nonEmptyArray()).add()).build();
    protected TagPattern[] patterns;

    @Override
    public com.hypixel.hytale.protocol.TagPattern toPacket() {
        com.hypixel.hytale.protocol.TagPattern packet = new com.hypixel.hytale.protocol.TagPattern();
        packet.operands = new com.hypixel.hytale.protocol.TagPattern[this.patterns.length];
        for (int i = 0; i < this.patterns.length; ++i) {
            packet.operands[i] = (com.hypixel.hytale.protocol.TagPattern)this.patterns[i].toPacket();
        }
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "MultiplePatternOp{patterns=" + Arrays.toString(this.patterns) + "} " + super.toString();
    }
}

