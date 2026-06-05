package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class AnchorPositionProvider extends PositionProvider {
   @Nonnull
   private final PositionProvider positionProvider;
   private final boolean isReversed;
   @Nonnull
   private final Bounds3d rOffsetBounds;
   @Nonnull
   private final PositionProvider.Context rChildContext;
   @Nonnull
   private final Vector3d rNewPosition;
   @Nonnull
   private final Vector3d rAnchor;
   @Nonnull
   private PositionProvider.Context rContext;
   @Nonnull
   private final Pipe.One<Vector3d> rChildPipe = new Pipe.One<Vector3d>() {
      public void accept(@NonNullDecl Vector3d position, @NonNullDecl Control control) {
         AnchorPositionProvider.this.rNewPosition.set(position);
         if (AnchorPositionProvider.this.isReversed) {
            AnchorPositionProvider.this.rNewPosition.sub(AnchorPositionProvider.this.rAnchor);
         } else {
            AnchorPositionProvider.this.rNewPosition.add(AnchorPositionProvider.this.rAnchor);
         }

         if (AnchorPositionProvider.this.rContext.bounds.contains(AnchorPositionProvider.this.rNewPosition)) {
            AnchorPositionProvider.this.rContext.pipe.accept(AnchorPositionProvider.this.rNewPosition, control);
         }
      }
   };

   public AnchorPositionProvider(@Nonnull PositionProvider positionProvider, boolean isReversed) {
      this.positionProvider = positionProvider;
      this.isReversed = isReversed;
      this.rOffsetBounds = new Bounds3d();
      this.rChildContext = new PositionProvider.Context();
      this.rNewPosition = new Vector3d();
      this.rAnchor = new Vector3d();
      this.rContext = new PositionProvider.Context();
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      this.rContext = context;
      if (context.anchor == null) {
         this.positionProvider.generate(context);
      } else {
         this.rAnchor.set(context.anchor);
         this.rOffsetBounds.assign(context.bounds);
         if (this.isReversed) {
            this.rOffsetBounds.offset(this.rAnchor);
         } else {
            this.rOffsetBounds.offsetOpposite(this.rAnchor);
         }

         this.rChildContext.assign(context);
         this.rChildContext.bounds = this.rOffsetBounds;
         this.rChildContext.pipe = this.rChildPipe;
         this.positionProvider.generate(this.rChildContext);
      }
   }
}
