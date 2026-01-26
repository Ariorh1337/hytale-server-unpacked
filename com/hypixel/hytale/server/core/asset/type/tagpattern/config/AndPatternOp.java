/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.tagpattern.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.TagPattern;
import com.hypixel.hytale.protocol.TagPatternType;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.MultiplePatternOp;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class AndPatternOp
extends MultiplePatternOp {
    @Nonnull
    public static BuilderCodec<AndPatternOp> CODEC = BuilderCodec.builder(AndPatternOp.class, AndPatternOp::new, MultiplePatternOp.CODEC).build();

    @Override
    public boolean test(Int2ObjectMap<IntSet> tags) {
        for (int i = 0; i < this.patterns.length; ++i) {
            if (this.patterns[i].test(tags)) continue;
            return false;
        }
        return true;
    }

    @Override
    public TagPattern toPacket() {
        TagPattern cached;
        TagPattern tagPattern = cached = this.cachedPacket == null ? null : (TagPattern)this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        TagPattern packet = super.toPacket();
        packet.type = TagPatternType.And;
        this.cachedPacket = new SoftReference<TagPattern>(packet);
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "AndPatternOp{} " + super.toString();
    }
}

