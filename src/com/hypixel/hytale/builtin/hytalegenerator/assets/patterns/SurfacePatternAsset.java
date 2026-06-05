package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.patterns.AndPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.ConstantPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.OrPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.SurfacePattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class SurfacePatternAsset extends PatternAsset {
   @Nonnull
   public static final BuilderCodec<SurfacePatternAsset> CODEC = BuilderCodec.builder(
         SurfacePatternAsset.class, SurfacePatternAsset::new, PatternAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Surface", PatternAsset.CODEC, true), (asset, value) -> asset.surface = value, asset -> asset.surface)
      .add()
      .append(new KeyedCodec<>("Medium", PatternAsset.CODEC, true), (asset, value) -> asset.origin = value, asset -> asset.origin)
      .add()
      .<Double>append(new KeyedCodec<>("SurfaceRadius", Codec.DOUBLE, false), (asset, value) -> asset.surfaceRadius = value, asset -> asset.surfaceRadius)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("MediumRadius", Codec.DOUBLE, false), (asset, value) -> asset.originRadius = value, asset -> asset.originRadius)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .<Integer>append(new KeyedCodec<>("SurfaceGap", Codec.INTEGER, false), (asset, value) -> asset.surfaceGap = value, asset -> asset.surfaceGap)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .<Integer>append(new KeyedCodec<>("MediumGap", Codec.INTEGER, false), (asset, value) -> asset.originGap = value, asset -> asset.originGap)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(new KeyedCodec<>("RequireAllFacings", Codec.BOOLEAN, false), (asset, value) -> asset.requireAllFacings = value, asset -> asset.requireAllFacings)
      .add()
      .append(
         new KeyedCodec<>("Facings", new ArrayCodec<>(SurfacePattern.Facing.CODEC, SurfacePattern.Facing[]::new), true),
         (asset, value) -> asset.facings = value,
         asset -> asset.facings
      )
      .add()
      .build();
   private PatternAsset surface = new ConstantPatternAsset();
   private PatternAsset origin = new ConstantPatternAsset();
   private double surfaceRadius;
   private double originRadius;
   private int surfaceGap;
   private int originGap;
   private SurfacePattern.Facing[] facings = new SurfacePattern.Facing[0];
   private boolean requireAllFacings;

   @Nonnull
   @Override
   public Pattern build(@Nonnull PatternAsset.Argument argument) {
      if (super.skip()) {
         return ConstantPattern.INSTANCE_FALSE;
      }

      Pattern floorPattern = this.surface.build(argument);
      Pattern originPattern = this.origin.build(argument);
      ArrayList<Pattern> patterns = new ArrayList<>(this.facings.length);

      for (SurfacePattern.Facing s : this.facings) {
         SurfacePattern pattern = new SurfacePattern(floorPattern, originPattern, this.surfaceRadius, this.originRadius, s, this.surfaceGap, this.originGap);
         patterns.add(pattern);
      }

      return this.requireAllFacings ? new AndPattern(patterns) : new OrPattern(patterns);
   }

   @Override
   public void cleanUp() {
      this.surface.cleanUp();
      this.origin.cleanUp();
   }
}
