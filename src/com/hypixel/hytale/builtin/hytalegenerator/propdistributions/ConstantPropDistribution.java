package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConstantPropDistribution extends PropDistribution {
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final Prop prop;
   @Nonnull
   private final PositionProvider.Context rPositionProviderContext;
   @Nonnull
   private final Control rControl;

   public ConstantPropDistribution(@Nonnull PositionProvider positionProvider, @Nonnull Prop prop) {
      this.positionProvider = positionProvider;
      this.prop = prop;
      this.rPositionProviderContext = new PositionProvider.Context();
      this.rControl = new Control();
   }

   @Override
   public void distribute(@NonNullDecl PropDistribution.Context context) {
      this.rControl.stop = false;
      this.rPositionProviderContext.bounds.min.assign(context.bounds.min);
      this.rPositionProviderContext.bounds.max.assign(context.bounds.max);
      this.rPositionProviderContext.pipe = (position, control) -> {
         if (this.rControl.stop) {
            control.stop = true;
         } else {
            context.pipe.accept(position, this.prop, this.rControl);
         }
      };
      this.positionProvider.generate(this.rPositionProviderContext);
   }

   @Override
   public void forEachPossibleProp(@NonNullDecl Consumer<Prop> consumer) {
      consumer.accept(this.prop);
   }
}
