package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class TeleportEffect extends TriggerEffect {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   @Nonnull
   public static final BuilderCodec<TeleportEffect> CODEC = BuilderCodec.builder(TeleportEffect.class, TeleportEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Position", Vector3dUtil.AS_ARRAY_CODEC), (e, v) -> e.position = v, e -> e.position)
      .add()
      .append(new KeyedCodec<>("World", Codec.STRING, false), (e, v) -> e.world = v, e -> e.world)
      .add()
      .append(new KeyedCodec<>("ResetVelocity", Codec.BOOLEAN, false), (e, v) -> e.resetVelocity = v, e -> e.resetVelocity)
      .add()
      .build();
   private Vector3d position;
   @Nullable
   private String world;
   private boolean resetVelocity = true;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.position != null) {
         Ref<EntityStore> entityRef = context.getEntityRef();
         Store<EntityStore> store = context.getStore();
         TransformComponent transformComponent = store.getComponent(entityRef, TransformComponent.getComponentType());
         if (transformComponent != null) {
            Teleport teleport = Teleport.createForPlayer(this.position, transformComponent.getRotation());
            if (!this.resetVelocity) {
               teleport = teleport.withoutVelocityReset();
            }

            if (this.world != null) {
               LOGGER.atWarning()
                  .log("TeleportEffect has 'World' set to '%s' but cross-world teleport is not yet supported; teleporting within current world", this.world);
            }

            store.addComponent(entityRef, Teleport.getComponentType(), teleport);
         }
      }
   }
}
