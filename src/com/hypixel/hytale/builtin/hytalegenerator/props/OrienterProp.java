package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.RotatorPattern;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OrienterProp extends Prop {
   @Nonnull
   private final List<Prop> props;
   @Nonnull
   private final List<Pattern> patterns;
   @Nonnull
   private final Scanner scanner;
   @Nonnull
   private final OrienterProp.SelectionMode selectionMode;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final Bounds3i readBounds;
   @Nonnull
   private final Bounds3i writeBounds;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final Pattern.Context rPatternContext;
   @Nonnull
   private final Prop.Context rChildContext;
   @Nonnull
   private final boolean[] rHasGenerated;
   @Nonnull
   private final List<Integer> rValidPatternIndices;

   public OrienterProp(
      @Nonnull List<RotationTuple> rotations,
      @Nonnull Prop prop,
      @Nonnull Pattern pattern,
      @Nonnull Scanner scanner,
      @Nonnull MaterialCache materialCache,
      @Nonnull OrienterProp.SelectionMode selectionMode,
      int seed
   ) {
      this.props = new ArrayList<>(rotations.size());
      this.patterns = new ArrayList<>(rotations.size());
      this.scanner = scanner;
      this.selectionMode = selectionMode;
      this.rngField = new RngField(seed);
      this.readBounds = new Bounds3i();
      this.writeBounds = new Bounds3i();
      this.random = new FastRandom();

      for (int i = 0; i < rotations.size(); i++) {
         Prop rotatedProp = new StaticRotatorProp(prop, rotations.get(i), materialCache);
         Pattern rotatedPattern = new RotatorPattern(pattern, rotations.get(i), materialCache);
         this.props.add(rotatedProp);
         this.patterns.add(rotatedPattern);
         Bounds3i rotatedReadBounds = scanner.getBoundsWithPattern_voxelGrid(rotatedPattern);
         Bounds3i rotatedPropReadBounds = rotatedProp.getReadBounds_voxelGrid();
         if (!rotatedPropReadBounds.isZeroVolume()) {
            rotatedReadBounds.stack(rotatedPropReadBounds);
         }

         this.readBounds.encompass(rotatedReadBounds);
         Bounds3i rotatedWriteBounds = rotatedProp.getWriteBounds_voxelGrid().clone();
         if (!rotatedWriteBounds.isZeroVolume()) {
            rotatedWriteBounds.stack(rotatedReadBounds);
         }

         this.writeBounds.encompass(rotatedWriteBounds);
      }

      this.rPatternContext = new Pattern.Context();
      this.rChildContext = new Prop.Context();
      this.rHasGenerated = new boolean[1];
      this.rValidPatternIndices = new ArrayList<>(this.patterns.size());
   }

   @Override
   public boolean generate(@NonNullDecl Prop.Context context) {
      this.rPatternContext.assign(context);
      this.rHasGenerated[0] = false;
      if (this.selectionMode != OrienterProp.SelectionMode.FIRST_VALID && this.selectionMode != OrienterProp.SelectionMode.ALL_VALID) {
         this.scanner.scan(context.position, (position, control) -> {
            this.rPatternContext.position = position;
            this.rValidPatternIndices.clear();

            for (int i = 0; i < this.patterns.size(); i++) {
               Pattern pattern = this.patterns.get(i);
               if (pattern.matches(this.rPatternContext)) {
                  this.rValidPatternIndices.add(i);
               }
            }

            if (!this.rValidPatternIndices.isEmpty()) {
               this.random.setSeed(this.rngField.get(position.x, position.y, position.z));
               int pickedIndex = this.random.nextInt(this.rValidPatternIndices.size());
               Prop prop = this.props.get(this.rValidPatternIndices.get(pickedIndex));
               this.rChildContext.assign(context);
               this.rChildContext.position = position;
               this.rHasGenerated[0] = prop.generate(this.rChildContext);
               control.stop = true;
            }
         });
      } else {
         this.scanner.scan(context.position, (position, control) -> {
            this.rPatternContext.position = position;

            for (int i = 0; i < this.patterns.size(); i++) {
               Pattern pattern = this.patterns.get(i);
               if (pattern.matches(this.rPatternContext)) {
                  Prop prop = this.props.get(i);
                  this.rChildContext.assign(context);
                  this.rChildContext.position = position;
                  this.rHasGenerated[0] = prop.generate(this.rChildContext);
                  control.stop = true;
                  if (this.selectionMode == OrienterProp.SelectionMode.FIRST_VALID) {
                     return;
                  }
               }
            }
         });
      }

      return this.rHasGenerated[0];
   }

   @NonNullDecl
   @Override
   public Bounds3i getReadBounds_voxelGrid() {
      return this.readBounds;
   }

   @NonNullDecl
   @Override
   public Bounds3i getWriteBounds_voxelGrid() {
      return this.writeBounds;
   }

   public enum SelectionMode {
      ALL_VALID,
      FIRST_VALID,
      RANDOM_VALID;
   }
}
