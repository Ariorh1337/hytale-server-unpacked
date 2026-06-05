package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.SimpleHorizontalPositionProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SimpleHorizontalPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<SimpleHorizontalPositionProviderAsset> CODEC = BuilderCodec.builder(
         SimpleHorizontalPositionProviderAsset.class, SimpleHorizontalPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("RangeY", RangeDoubleAsset.CODEC, true), (asset, value) -> asset.rangeYAsset = value, asset -> asset.rangeYAsset)
      .add()
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .build();
   private RangeDoubleAsset rangeYAsset = new RangeDoubleAsset();
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      PositionProvider positionProvider = this.positionProviderAsset.build(argument);
      return new SimpleHorizontalPositionProvider(this.rangeYAsset.build(), positionProvider);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
