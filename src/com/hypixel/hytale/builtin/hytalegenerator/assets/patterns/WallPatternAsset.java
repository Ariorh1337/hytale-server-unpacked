package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.patterns.ConstantPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.WallPattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.List;
import javax.annotation.Nonnull;

public class WallPatternAsset extends PatternAsset {
   @Nonnull
   public static final BuilderCodec<WallPatternAsset> CODEC = BuilderCodec.builder(WallPatternAsset.class, WallPatternAsset::new, PatternAsset.ABSTRACT_CODEC)
      .append(new KeyedCodec<>("Wall", PatternAsset.CODEC, true), (asset, value) -> asset.wall = value, asset -> asset.wall)
      .add()
      .append(new KeyedCodec<>("Origin", PatternAsset.CODEC, true), (asset, value) -> asset.origin = value, asset -> asset.origin)
      .add()
      .append(new KeyedCodec<>("RequireAllDirections", Codec.BOOLEAN, false), (asset, value) -> asset.matchAll = value, asset -> asset.matchAll)
      .add()
      .append(
         new KeyedCodec<>("Directions", new ArrayCodec<>(WallPattern.WallDirection.CODEC, WallPattern.WallDirection[]::new), true),
         (asset, value) -> asset.directions = value,
         asset -> asset.directions
      )
      .add()
      .build();
   private PatternAsset wall = new ConstantPatternAsset();
   private PatternAsset origin = new ConstantPatternAsset();
   private WallPattern.WallDirection[] directions = new WallPattern.WallDirection[0];
   private boolean matchAll = false;

   @Nonnull
   @Override
   public Pattern build(@Nonnull PatternAsset.Argument argument) {
      if (super.skip()) {
         return ConstantPattern.INSTANCE_FALSE;
      }

      Pattern wallPattern = this.wall.build(argument);
      Pattern originPattern = this.origin.build(argument);
      return new WallPattern(wallPattern, originPattern, List.of(this.directions), this.matchAll);
   }

   @Override
   public void cleanUp() {
      this.wall.cleanUp();
      this.origin.cleanUp();
   }
}
