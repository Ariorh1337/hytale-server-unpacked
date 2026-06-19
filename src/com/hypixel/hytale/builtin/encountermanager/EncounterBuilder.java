package com.hypixel.hytale.builtin.encountermanager;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import javax.annotation.Nonnull;

public interface EncounterBuilder {
   @Nonnull
   EncounterManager createAndAttach(@Nonnull Holder<EntityStore> var1, @Nonnull BuilderSupport var2);
}
