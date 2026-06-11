package com.hypixel.hytale.server.npc.blackboard.view.attitude;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public interface IAttitudeProvider {
   int OVERRIDE_PRIORITY = 0;

   Attitude getAttitude(@Nonnull Ref<EntityStore> var1, int var2, @Nonnull Ref<EntityStore> var3, @Nonnull ComponentAccessor<EntityStore> var4);
}
