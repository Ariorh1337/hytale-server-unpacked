package com.hypixel.hytale.builtin.hytalegenerator;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GridGraphCache;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import org.joml.Vector3d;

public class VectorAssetValidatorUtil {
   public static Validator<Vector3d> greaterThan(double value) {
      return new Validator<Vector3d>() {
         public void accept(Vector3d value, ValidationResults results) {
            if (!GridGraphCache.isValidCellSize(value)) {
               results.fail("Vector must have all components greater than 0, got " + value);
            }
         }

         @Override
         public void updateSchema(SchemaContext context, Schema target) {
         }
      };
   }
}
