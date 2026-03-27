package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class CharacterCollisionData extends BasicCollisionData {
   public Ref<EntityStore> entityReference;
   public boolean isPlayer;

   public void assign(@Nonnull Vector3d collisionPoint, double collisionVectorScale, Ref<EntityStore> entityReference, boolean isPlayer) {
      this.collisionPoint.set(collisionPoint);
      this.collisionStart = collisionVectorScale;
      this.entityReference = entityReference;
      this.isPlayer = isPlayer;
   }
}
