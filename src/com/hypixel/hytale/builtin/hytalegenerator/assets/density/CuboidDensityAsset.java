package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.CubeDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ScaleDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class CuboidDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<CuboidDensityAsset> CODEC = BuilderCodec.builder(
         CuboidDensityAsset.class, CuboidDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Curve", CurveAsset.CODEC, true), (asset, value) -> asset.densityCurveAsset = value, asset -> asset.densityCurveAsset)
      .add()
      .<Vector3d>append(new KeyedCodec<>("Scale", Vector3dUtil.CODEC, false), (asset, value) -> asset.scaleVector = value, asset -> asset.scaleVector)
      .addValidator(new Validator<Vector3d>() {
         public void accept(Vector3d v, ValidationResults r) {
            if (v.x == 0.0 || v.y == 0.0 || v.z == 0.0) {
               r.fail("scale vector contains 0.0");
            }
         }

         @Override
         public void updateSchema(SchemaContext context, Schema target) {
         }
      })
      .add()
      .append(new KeyedCodec<>("NewYAxis", Vector3dUtil.CODEC, false), (asset, value) -> {
         if (value.length() != 0.0) {
            asset.newYAxis = value;
         }
      }, asset -> asset.newYAxis)
      .add()
      .append(new KeyedCodec<>("Spin", Codec.DOUBLE, false), (asset, value) -> asset.spinAngle = value, asset -> asset.spinAngle)
      .add()
      .build();
   private CurveAsset densityCurveAsset = new ConstantCurveAsset();
   private Vector3d scaleVector = new Vector3d(1.0, 1.0, 1.0);
   @Nonnull
   private Vector3d newYAxis = new Vector3d(0.0, 1.0, 0.0);
   private double spinAngle;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (!this.isSkipped() && this.densityCurveAsset != null) {
         CubeDensity cube = new CubeDensity(this.densityCurveAsset.build());
         ScaleDensity scale = new ScaleDensity(this.scaleVector.x, this.scaleVector.y, this.scaleVector.z, cube);
         return new RotatorDensity(scale, this.newYAxis, this.spinAngle);
      } else {
         return new ConstantValueDensity(0.0);
      }
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      this.densityCurveAsset.cleanUp();
   }
}
