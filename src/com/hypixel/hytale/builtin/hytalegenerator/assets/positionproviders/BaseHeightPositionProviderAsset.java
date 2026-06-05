package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.DecimalConstantsFrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.OffsetPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class BaseHeightPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<BaseHeightPositionProviderAsset> CODEC = BuilderCodec.builder(
         BaseHeightPositionProviderAsset.class, BaseHeightPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("MinYRead", Codec.DOUBLE, false), (asset, value) -> asset.minYRead = value, asset -> asset.minYRead)
      .add()
      .append(new KeyedCodec<>("MaxYRead", Codec.DOUBLE, false), (asset, value) -> asset.maxYRead = value, asset -> asset.maxYRead)
      .add()
      .append(new KeyedCodec<>("BedName", Codec.STRING, false), (asset, value) -> asset.baseHeightName = value, asset -> asset.baseHeightName)
      .add()
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .build();
   private double minYRead = -1.0;
   private double maxYRead = 1.0;
   private String baseHeightName = "";
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      PositionProvider positionProvider = this.positionProviderAsset.build(argument);
      Double baseHeight = DecimalConstantsFrameworkAsset.Entries.get(this.baseHeightName, argument.referenceBundle);
      if (baseHeight == null) {
         baseHeight = 0.0;
      }

      return new OffsetPositionProvider(new Vector3d(0.0, baseHeight, 0.0), positionProvider);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
