package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class VectorOffsetPositionProvider extends PositionProvider {
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final Bounds3d movementBounds;
   @Nonnull
   private final Bounds3d movementBoundsFlipped;
   @Nonnull
   private final Bounds3d rChildBounds;
   @Nonnull
   private final Bounds3d rLocalMovementBounds;
   @Nonnull
   private final PositionProvider.Context rChildContext;
   @Nonnull
   private final VectorProvider.Context rVectorContext;
   @Nonnull
   private PositionProvider.Context rContext;
   @Nonnull
   private final Vector3d rVector;
   @Nonnull
   private final Vector3d rPosition;
   @Nonnull
   private final Pipe.One<Vector3d> rChildPipe = new Pipe.One<Vector3d>() {
      public void accept(@NonNullDecl Vector3d position, @NonNullDecl Control control) {
         VectorOffsetPositionProvider.this.rVectorContext.assign(VectorOffsetPositionProvider.this.rContext, position);
         VectorOffsetPositionProvider.this.vectorProvider.process(VectorOffsetPositionProvider.this.rVectorContext, VectorOffsetPositionProvider.this.rVector);
         VectorOffsetPositionProvider.this.rLocalMovementBounds.assign(VectorOffsetPositionProvider.this.movementBounds).offset(position);
         VectorOffsetPositionProvider.this.rPosition.set(position);
         VectorOffsetPositionProvider.this.rPosition.add(VectorOffsetPositionProvider.this.rVector);
         if (VectorOffsetPositionProvider.this.rLocalMovementBounds.contains(VectorOffsetPositionProvider.this.rPosition)) {
            position.set(VectorOffsetPositionProvider.this.rPosition);
         }

         VectorOffsetPositionProvider.this.rContext.pipe.accept(position, control);
      }
   };

   public VectorOffsetPositionProvider(@Nonnull VectorProvider vectorProvider, @Nonnull PositionProvider positionProvider, @Nonnull Bounds3d movementBounds) {
      this.vectorProvider = vectorProvider;
      this.positionProvider = positionProvider;
      this.movementBounds = movementBounds.clone();
      this.movementBoundsFlipped = this.movementBounds.clone().flipOnOriginPoint();
      this.rChildBounds = new Bounds3d();
      this.rLocalMovementBounds = new Bounds3d();
      this.rVectorContext = new VectorProvider.Context();
      this.rChildContext = new PositionProvider.Context();
      this.rContext = new PositionProvider.Context();
      this.rVector = new Vector3d();
      this.rPosition = new Vector3d();
   }

   @Override
   public void generate(@NonNullDecl PositionProvider.Context context) {
      this.rContext = context;
      this.rChildBounds.assign(context.bounds);
      this.rChildBounds.stack(this.movementBoundsFlipped);
      this.rChildContext.assign(context);
      this.rChildContext.bounds = this.rChildBounds;
      this.rChildContext.pipe = this.rChildPipe;
      this.positionProvider.generate(this.rChildContext);
   }
}
