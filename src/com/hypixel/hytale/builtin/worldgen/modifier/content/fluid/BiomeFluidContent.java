package com.hypixel.hytale.builtin.worldgen.modifier.content.fluid;

import com.hypixel.hytale.builtin.worldgen.modifier.content.Codecs;
import com.hypixel.hytale.builtin.worldgen.modifier.content.Content;
import com.hypixel.hytale.builtin.worldgen.modifier.content.common.NoiseMask;
import com.hypixel.hytale.builtin.worldgen.modifier.event.ModifyEvent;
import com.hypixel.hytale.builtin.worldgen.modifier.event.ModifyEvents;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.procedurallib.supplier.DoubleRange;
import com.hypixel.hytale.procedurallib.supplier.DoubleRangeNoiseSupplier;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.worldgen.container.WaterContainer;

public class BiomeFluidContent implements Content {
   public static final String ID = "BiomeFluid";
   public static final BuilderCodec<BiomeFluidContent> CODEC = BuilderCodec.builder(BiomeFluidContent.class, BiomeFluidContent::new)
      .documentation("Define a fluid that should generate between certain heights in the biome")
      .<String>append(new KeyedCodec<>("Fluid", Codecs.FLUID_TYPE), (t, v) -> t.fluid = v, t -> t.fluid)
      .documentation("The fluid type to place")
      .add()
      .<String>append(new KeyedCodec<>("Block", Codecs.BLOCK_TYPE), (t, v) -> t.block = v, t -> t.block)
      .documentation("The block type to place in the fluid")
      .add()
      .<IntRange>append(new KeyedCodec<>("MinY", Codecs.INT_RANGE), (t, v) -> t.minY = v, t -> t.minY)
      .documentation("The minimum height that the fluid should generate at")
      .add()
      .<NoiseMask>append(new KeyedCodec<>("MinYNoise", NoiseMask.CODEC), (t, v) -> t.minYNoise = v, t -> t.minYNoise)
      .documentation("The noise used to modulate the min-y height")
      .add()
      .<IntRange>append(new KeyedCodec<>("MaxY", Codecs.INT_RANGE), (t, v) -> t.maxY = v, t -> t.maxY)
      .documentation("The maximum height that the fluid should generate at")
      .add()
      .<NoiseMask>append(new KeyedCodec<>("MaxYNoise", NoiseMask.CODEC), (t, v) -> t.maxYNoise = v, t -> t.maxYNoise)
      .documentation("The noise used to modulate the max-y height")
      .add()
      .<NoiseMask>append(new KeyedCodec<>("NoiseMask", NoiseMask.CODEC), (t, v) -> t.noiseMask = v, t -> t.noiseMask)
      .documentation("The noise mask used to determine where the fluid should be placed")
      .add()
      .build();
   protected static final IntRange DEFAULT_MIN = new IntRange(0, 0);
   protected static final IntRange DEFAULT_MAX = new IntRange(115, 115);
   protected String fluid = "Empty";
   protected String block = "Empty";
   protected IntRange minY = DEFAULT_MIN;
   protected NoiseMask minYNoise = NoiseMask.ZERO;
   protected IntRange maxY = DEFAULT_MAX;
   protected NoiseMask maxYNoise = NoiseMask.ZERO;
   protected NoiseMask noiseMask = NoiseMask.DEFAULT;

   @Override
   public Content.Type type() {
      return Content.Type.BIOME_FLUID;
   }

   @Override
   public <T> void applyTo(ModifyEvent<T> event) throws Exception {
      if (event instanceof ModifyEvents.BiomeFluids container) {
         int fluidId = Fluid.getAssetMap().getIndexOrDefault(this.fluid, 0);
         int blockId = BlockType.getAssetMap().getIndexOrDefault(this.block, 0);
         container.entries()
            .add(
               new WaterContainer.Entry(
                  blockId,
                  fluidId,
                  new DoubleRangeNoiseSupplier(
                     new DoubleRange.Normal(
                        Math.min(this.minY.getInclusiveMin(), this.minY.getInclusiveMax()), Math.max(this.minY.getInclusiveMin(), this.minY.getInclusiveMax())
                     ),
                     this.minYNoise.buildNoise(event.seed())
                  ),
                  new DoubleRangeNoiseSupplier(
                     new DoubleRange.Normal(
                        Math.min(this.maxY.getInclusiveMin(), this.maxY.getInclusiveMax()), Math.max(this.maxY.getInclusiveMin(), this.maxY.getInclusiveMax())
                     ),
                     this.maxYNoise.buildNoise(event.seed())
                  ),
                  this.noiseMask.build(event.seed())
               )
            );
      }
   }
}
