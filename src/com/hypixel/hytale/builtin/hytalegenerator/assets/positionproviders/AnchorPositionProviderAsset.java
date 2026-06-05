package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.AnchorPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class AnchorPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<AnchorPositionProviderAsset> CODEC = BuilderCodec.builder(
         AnchorPositionProviderAsset.class, AnchorPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Reversed", Codec.BOOLEAN, true), (asset, value) -> asset.isReversed = value, asset -> asset.isReversed)
      .add()
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .build();
   private boolean isReversed = false;
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      PositionProvider positionProvider = this.positionProviderAsset == null ? EmptyPositionProvider.INSTANCE : this.positionProviderAsset.build(argument);
      return new AnchorPositionProvider(positionProvider, this.isReversed);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
