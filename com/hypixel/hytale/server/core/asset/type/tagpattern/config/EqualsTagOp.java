/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.tagpattern.config;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.TagPatternType;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.TagPattern;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class EqualsTagOp
extends TagPattern {
    @Nonnull
    public static BuilderCodec<EqualsTagOp> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(EqualsTagOp.class, EqualsTagOp::new, TagPattern.BASE_CODEC).append(new KeyedCodec<String>("Tag", Codec.STRING), (singleTagOp, s) -> {
        singleTagOp.tag = s;
    }, singleTagOp -> singleTagOp.tag).addValidator(Validators.nonNull()).add()).afterDecode(singleTagOp -> {
        if (singleTagOp.tag == null) {
            return;
        }
        singleTagOp.tagIndex = AssetRegistry.getOrCreateTagIndex(singleTagOp.tag);
    })).build();
    protected String tag;
    protected int tagIndex;

    public EqualsTagOp(String tag) {
        this.tag = tag;
    }

    protected EqualsTagOp() {
    }

    @Override
    public boolean test(@Nonnull Int2ObjectMap<IntSet> tags) {
        return tags.containsKey(this.tagIndex);
    }

    @Override
    public com.hypixel.hytale.protocol.TagPattern toPacket() {
        com.hypixel.hytale.protocol.TagPattern cached;
        com.hypixel.hytale.protocol.TagPattern tagPattern = cached = this.cachedPacket == null ? null : (com.hypixel.hytale.protocol.TagPattern)this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.TagPattern packet = new com.hypixel.hytale.protocol.TagPattern();
        packet.type = TagPatternType.Equals;
        packet.tagIndex = this.tagIndex;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.TagPattern>(packet);
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "EqualsTagOp{tag='" + this.tag + "'} " + super.toString();
    }
}

