package com.hypixel.hytale.server.npc.role.builders;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;

public interface RoleBuilder {
   @Nonnull
   Role createAndAttach(@Nonnull Holder<EntityStore> var1, @Nonnull BuilderSupport var2);
}
