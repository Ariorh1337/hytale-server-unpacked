package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.io.NetworkSerializable;

public class ItemMovementSettings implements NetworkSerializable<com.hypixel.hytale.protocol.ItemMovementSettings> {
   public static final BuilderCodec<ItemMovementSettings> CODEC = BuilderCodec.builder(ItemMovementSettings.class, ItemMovementSettings::new)
      .append(
         new KeyedCodec<>("ExtraJumpCount", Codec.INTEGER),
         (itemMovementSettings, integer) -> itemMovementSettings.extraJumpCount = integer,
         itemMovementSettings -> itemMovementSettings.extraJumpCount
      )
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(
         new KeyedCodec<>("ExtraJumpParticleSystem", Codec.STRING),
         (itemMovementSettings, particleSystem) -> itemMovementSettings.extraJumpParticleSystem = particleSystem,
         itemMovementSettings -> itemMovementSettings.extraJumpParticleSystem
      )
      .add()
      .build();
   protected int extraJumpCount;
   protected String extraJumpParticleSystem;

   public com.hypixel.hytale.protocol.ItemMovementSettings toPacket() {
      return new com.hypixel.hytale.protocol.ItemMovementSettings(this.extraJumpCount, this.extraJumpParticleSystem);
   }
}
