package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.AxisDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
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

public class AxisDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<AxisDensityAsset> CODEC = BuilderCodec.builder(AxisDensityAsset.class, AxisDensityAsset::new, DensityAsset.ABSTRACT_CODEC)
      .append(new KeyedCodec<>("Curve", CurveAsset.CODEC, true), (asset, value) -> asset.distanceCurveAsset = value, asset -> asset.distanceCurveAsset)
      .add()
      .append(new KeyedCodec<>("IsAnchored", Codec.BOOLEAN, false), (asset, value) -> asset.isAnchored = value, asset -> asset.isAnchored)
      .add()
      .<Vector3d>append(new KeyedCodec<>("Axis", Vector3dUtil.CODEC, false), (asset, value) -> asset.axis = value, asset -> asset.axis)
      .addValidator(new Validator<Vector3d>() {
         public void accept(Vector3d v, ValidationResults r) {
            if (v.length() == 0.0) {
               r.fail("Axis can't be a zero vector.");
            }
         }

         @Override
         public void updateSchema(SchemaContext context, Schema target) {
         }
      })
      .add()
      .build();
   private CurveAsset distanceCurveAsset = new ConstantCurveAsset();
   private Vector3d axis = new Vector3d(0.0, 1.0, 0.0);
   private boolean isAnchored = false;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return !this.isSkipped() && this.distanceCurveAsset != null
         ? new AxisDensity(this.distanceCurveAsset.build(), this.axis, this.isAnchored)
         : new ConstantValueDensity(0.0);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      this.distanceCurveAsset.cleanUp();
   }
}
