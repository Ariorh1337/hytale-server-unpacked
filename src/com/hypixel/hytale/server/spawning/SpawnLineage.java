package com.hypixel.hytale.server.spawning;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnLineage implements Component<EntityStore> {
   public static final BuilderCodec<SpawnLineage> CODEC = BuilderCodec.builder(SpawnLineage.class, SpawnLineage::new)
      .append(new KeyedCodec<>("LineageId", Codec.STRING), (component, id) -> component.lineageId = id, component -> component.lineageId)
      .add()
      .build();
   @Nullable
   private String lineageId;

   @Nonnull
   public static ComponentType<EntityStore, SpawnLineage> getComponentType() {
      return NPCPlugin.get().getSpawnLineageComponentType();
   }

   public static void inherit(@Nonnull Ref<EntityStore> parent, @Nonnull Ref<EntityStore> child, @Nonnull Store<EntityStore> store) {
      ComponentType<EntityStore, SpawnLineage> type = getComponentType();
      if (!store.getArchetype(child).contains(type)) {
         SpawnLineage parentLineage = store.getComponent(parent, type);
         if (parentLineage != null && parentLineage.lineageId != null) {
            store.addComponent(child, type, new SpawnLineage(parentLineage.lineageId));
         }
      }
   }

   public SpawnLineage() {
   }

   public SpawnLineage(@Nonnull String lineageId) {
      this.lineageId = lineageId;
   }

   @Nullable
   public String getLineageId() {
      return this.lineageId;
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      SpawnLineage copy = new SpawnLineage();
      copy.lineageId = this.lineageId;
      return copy;
   }
}
