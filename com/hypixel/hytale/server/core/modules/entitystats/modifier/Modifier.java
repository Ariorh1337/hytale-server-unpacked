/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats.modifier;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Modifier
implements NetworkSerializable<com.hypixel.hytale.protocol.Modifier> {
    public static final CodecMapCodec<Modifier> CODEC = new CodecMapCodec();
    protected static final BuilderCodec<Modifier> BASE_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(Modifier.class).append(new KeyedCodec<ModifierTarget>("Target", new EnumCodec<ModifierTarget>(ModifierTarget.class, EnumCodec.EnumStyle.LEGACY)), (regenerating, value) -> {
        regenerating.target = value;
    }, regenerating -> regenerating.target).add()).build();
    protected ModifierTarget target = ModifierTarget.MAX;

    public Modifier() {
    }

    public Modifier(ModifierTarget target) {
        this.target = target;
    }

    public abstract float apply(float var1);

    public ModifierTarget getTarget() {
        return this.target;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Modifier toPacket() {
        if (!(this instanceof StaticModifier)) {
            throw new UnsupportedOperationException("Only static modifiers supported on the client currently.");
        }
        com.hypixel.hytale.protocol.Modifier packet = new com.hypixel.hytale.protocol.Modifier();
        packet.target = switch (this.target.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> com.hypixel.hytale.protocol.ModifierTarget.Max;
            case 0 -> com.hypixel.hytale.protocol.ModifierTarget.Min;
        };
        return packet;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Modifier modifier = (Modifier)o;
        return this.target == modifier.target;
    }

    public int hashCode() {
        return this.target != null ? this.target.hashCode() : 0;
    }

    @Nonnull
    public String toString() {
        return "Modifier{target=" + String.valueOf((Object)this.target) + "}";
    }

    public static enum ModifierTarget {
        MIN,
        MAX;

        public static final ModifierTarget[] VALUES;

        static {
            VALUES = ModifierTarget.values();
        }
    }
}

