package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.bounds.DecimalBounds3dAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.VectorOffsetPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class VectorOffsetPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<VectorOffsetPositionProviderAsset> CODEC = BuilderCodec.builder(
         VectorOffsetPositionProviderAsset.class, VectorOffsetPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("VectorProvider", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.vectorProviderAsset = value,
         asset -> asset.vectorProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("MovementBounds", DecimalBounds3dAsset.CODEC, true),
         (asset, value) -> asset.movementBoundsAsset = value,
         asset -> asset.movementBoundsAsset
      )
      .add()
      .build();
   @Nonnull
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private DecimalBounds3dAsset movementBoundsAsset = DecimalBounds3dAsset.ZERO_INSTANCE;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      PositionProvider positionProvider = this.positionProviderAsset.build(argument);
      VectorProvider vectorProvider = this.vectorProviderAsset.build(new VectorProviderAsset.Argument(argument));
      Bounds3d movementBounds = this.movementBoundsAsset.build();
      return new VectorOffsetPositionProvider(vectorProvider, positionProvider, movementBounds);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
      this.vectorProviderAsset.cleanUp();
   }
}
