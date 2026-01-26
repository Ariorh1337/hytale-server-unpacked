/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.tagpattern.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.TagPatternType;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.TagPattern;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class NotPatternOp
extends TagPattern {
    @Nonnull
    public static BuilderCodec<NotPatternOp> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(NotPatternOp.class, NotPatternOp::new, TagPattern.BASE_CODEC).append(new KeyedCodec("Pattern", TagPattern.CODEC), (singleTagOp, s) -> {
        singleTagOp.pattern = s;
    }, singleTagOp -> singleTagOp.pattern).addValidator(Validators.nonNull()).add()).build();
    protected TagPattern pattern;

    @Override
    public boolean test(Int2ObjectMap<IntSet> tags) {
        return !this.pattern.test(tags);
    }

    @Override
    public com.hypixel.hytale.protocol.TagPattern toPacket() {
        com.hypixel.hytale.protocol.TagPattern cached;
        com.hypixel.hytale.protocol.TagPattern tagPattern = cached = this.cachedPacket == null ? null : (com.hypixel.hytale.protocol.TagPattern)this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.TagPattern packet = new com.hypixel.hytale.protocol.TagPattern();
        packet.type = TagPatternType.Not;
        packet.not = (com.hypixel.hytale.protocol.TagPattern)this.pattern.toPacket();
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.TagPattern>(packet);
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "NotPatternOp{pattern=" + String.valueOf(this.pattern) + "} " + super.toString();
    }
}

