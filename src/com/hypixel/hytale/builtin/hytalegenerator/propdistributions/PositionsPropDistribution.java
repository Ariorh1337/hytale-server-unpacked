package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PositionsPropDistribution extends PropDistribution {
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final PositionProvider.Context rPositionProviderContext;

   public PositionsPropDistribution(@Nonnull PositionProvider positionProvider) {
      this.positionProvider = positionProvider;
      this.rPositionProviderContext = new PositionProvider.Context();
   }

   @Override
   public void distribute(@Nonnull PropDistribution.Context context) {
      this.rPositionProviderContext.assign(context);
      this.rPositionProviderContext.pipe = (position, control) -> {
         assert context.bounds.contains(position);
         context.pipe.accept(position, EmptyProp.INSTANCE, control);
      };
      this.positionProvider.generate(this.rPositionProviderContext);
   }

   @Override
   public void forEachPossibleProp(@NonNullDecl Consumer<Prop> consumer) {
   }
}
