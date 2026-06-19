package com.hypixel.hytale.server.npc.components.messaging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

@FunctionalInterface
public interface BeaconReceiverProvider {
   void collectInRange(@Nonnull Vector3d var1, double var2, @Nonnull Store<EntityStore> var4, @Nonnull List<Ref<EntityStore>> var5);
}
