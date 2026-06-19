package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public enum BreakShapeDurabilityMode {
   PerSwing,
   PerBlock;

   @Nonnull
   public static final EnumCodec<BreakShapeDurabilityMode> CODEC = new EnumCodec<>(BreakShapeDurabilityMode.class);
}
