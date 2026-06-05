package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class AnchorPropDistribution extends PropDistribution {
   @Nonnull
   private final PropDistribution propDistribution;
   private final boolean isReversed;
   @Nonnull
   private final Bounds3d rOffsetBounds;
   @Nonnull
   private final PropDistribution.Context rChildContext;
   @Nonnull
   private final Vector3d rNewPosition;
   @Nonnull
   private final Vector3d rAnchor;
   @Nonnull
   private PropDistribution.Context rContext;
   @Nonnull
   private final Pipe.Two<Vector3d, Prop> rChildPipe = new Pipe.Two<Vector3d, Prop>() {
      public void accept(@NonNullDecl Vector3d position, @Nonnull Prop prop, @NonNullDecl Control control) {
         AnchorPropDistribution.this.rNewPosition.set(position);
         if (AnchorPropDistribution.this.isReversed) {
            AnchorPropDistribution.this.rNewPosition.sub(AnchorPropDistribution.this.rAnchor);
         } else {
            AnchorPropDistribution.this.rNewPosition.add(AnchorPropDistribution.this.rAnchor);
         }

         if (AnchorPropDistribution.this.rContext.bounds.contains(AnchorPropDistribution.this.rNewPosition)) {
            AnchorPropDistribution.this.rContext.pipe.accept(AnchorPropDistribution.this.rNewPosition, prop, control);
         }
      }
   };

   public AnchorPropDistribution(@Nonnull PropDistribution propDistribution, boolean isReversed) {
      this.propDistribution = propDistribution;
      this.isReversed = isReversed;
      this.rOffsetBounds = new Bounds3d();
      this.rChildContext = new PropDistribution.Context();
      this.rNewPosition = new Vector3d();
      this.rAnchor = new Vector3d();
      this.rContext = new PropDistribution.Context();
   }

   @Override
   public void distribute(@NonNullDecl PropDistribution.Context context) {
      this.rContext = context;
      if (context.anchor == null) {
         this.propDistribution.distribute(context);
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
         this.propDistribution.distribute(this.rChildContext);
      }
   }

   @Override
   public void forEachPossibleProp(@NonNullDecl Consumer<Prop> consumer) {
      this.propDistribution.forEachPossibleProp(consumer);
   }
}
