package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.DirectionalJitterPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class DirectionalJitterPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<DirectionalJitterPositionProviderAsset> CODEC = BuilderCodec.builder(
         DirectionalJitterPositionProviderAsset.class, DirectionalJitterPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Magnitude", Codec.DOUBLE, true), (asset, value) -> asset.magnitude = value, asset -> asset.magnitude)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("IsBidirectional", Codec.BOOLEAN, true), (asset, value) -> asset.isBidirectional = value, asset -> asset.isBidirectional)
      .add()
      .append(new KeyedCodec<>("Direction", Vector3dUtil.CODEC, true), (asset, value) -> asset.direction = value, asset -> asset.direction)
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .build();
   private double magnitude = 0.0;
   private boolean isBidirectional = true;
   @Nonnull
   private Vector3d direction = new Vector3d(0.0, 1.0, 1.0);
   @Nonnull
   private String seed = "";
   @Nonnull
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      SeedBox seedBox = argument.parentSeed.child(this.seed);
      PositionProvider positionProvider = this.positionProviderAsset.build(argument);
      return new DirectionalJitterPositionProvider(this.magnitude, this.isBidirectional, this.direction, seedBox.createSupplier().get(), positionProvider);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
