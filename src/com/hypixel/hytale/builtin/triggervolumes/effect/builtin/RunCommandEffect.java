package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public class RunCommandEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<RunCommandEffect> CODEC = BuilderCodec.builder(RunCommandEffect.class, RunCommandEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Command", Codec.STRING), (e, v) -> e.command = v, e -> e.command)
      .add()
      .build();
   private String command;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.command != null) {
         PlayerRef playerRef = context.getStore().getComponent(context.getEntityRef(), PlayerRef.getComponentType());
         if (playerRef != null) {
            CommandManager.get().handleCommand(playerRef, this.command);
         }
      }
   }
}
