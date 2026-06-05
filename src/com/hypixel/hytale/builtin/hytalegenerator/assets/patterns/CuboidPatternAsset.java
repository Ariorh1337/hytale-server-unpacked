package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.patterns.ConstantPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.CuboidPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3iUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3i;

public class CuboidPatternAsset extends PatternAsset {
   @Nonnull
   public static final BuilderCodec<CuboidPatternAsset> CODEC = BuilderCodec.builder(
         CuboidPatternAsset.class, CuboidPatternAsset::new, PatternAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("SubPattern", PatternAsset.CODEC, true), (asset, value) -> asset.subPatternAsset = value, asset -> asset.subPatternAsset)
      .add()
      .append(new KeyedCodec<>("Min", Vector3iUtil.CODEC, true), (asset, value) -> asset.min = value, asset -> asset.min)
      .add()
      .append(new KeyedCodec<>("Max", Vector3iUtil.CODEC, true), (asset, value) -> asset.max = value, asset -> asset.max)
      .add()
      .build();
   private PatternAsset subPatternAsset = new ConstantPatternAsset();
   private Vector3i min = new Vector3i();
   private Vector3i max = new Vector3i();

   @Nonnull
   @Override
   public Pattern build(@Nonnull PatternAsset.Argument argument) {
      if (super.skip()) {
         return ConstantPattern.INSTANCE_FALSE;
      }

      Pattern subPattern = this.subPatternAsset.build(argument);
      return new CuboidPattern(subPattern, this.min, this.max);
   }

   @Override
   public void cleanUp() {
      this.subPatternAsset.cleanUp();
   }
}
