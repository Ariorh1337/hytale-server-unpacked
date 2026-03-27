package com.hypixel.hytale.builtin.worldgen.modifier.content.common;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.procedurallib.condition.BasicHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import javax.annotation.Nonnull;

public class HeightMask {
   public static final BuilderCodec<HeightMask> CODEC = BuilderCodec.builder(HeightMask.class, HeightMask::new)
      .documentation("Defines a height mask that restricts placement to a specific vertical range within the world")
      .<Integer>append(new KeyedCodec<>("Min", Codec.INTEGER), (i, v) -> i.range.setInclusiveMin(v), i -> i.range.getInclusiveMin())
      .addValidator(Validators.range(0, 319))
      .documentation("The inclusive minimum height of the mask range")
      .add()
      .<Integer>append(new KeyedCodec<>("Max", Codec.INTEGER), (i, v) -> i.range.setInclusiveMax(v), i -> i.range.getInclusiveMax())
      .addValidator(Validators.range(0, 319))
      .documentation("The inclusive maximum height of the mask range")
      .add()
      .afterDecode(HeightMask::rebuild)
      .build();
   public static final IHeightThresholdInterpreter DEFAULT_HEIGHT_THRESHOLD = new BasicHeightThresholdInterpreter(
      new int[]{0, 319}, new float[]{1.0F, 1.0F}, 320
   );
   public static final HeightMask DEFAULT = new HeightMask() {
      {
         this.rebuild();
      }
   };
   protected IntRange range = new IntRange(0, 319);
   @Nonnull
   protected transient ICoordinateRndCondition condition = DefaultCoordinateRndCondition.DEFAULT_TRUE;

   @Nonnull
   public ICoordinateRndCondition getCondition() {
      return this.condition;
   }

   protected void rebuild() {
      IntRange range = this.range;
      this.condition = (seed, x, z, y, random) -> y >= range.getInclusiveMin() && y <= range.getInclusiveMax();
   }
}
