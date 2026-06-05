package com.hypixel.hytale.builtin.adventure.wilderness;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessTracker;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessVisitor;
import com.hypixel.hytale.builtin.adventure.wilderness.system.WildernessTrackerSystems;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class WildernessPlugin extends JavaPlugin {
   protected static WildernessPlugin instance;
   protected ResourceType<ChunkStore, WildernessTracker> wildernessTrackerResourceType;
   protected ResourceType<ChunkStore, WildernessVisitor.ChunkVisitor> chunkStoreResourceType;
   protected ResourceType<EntityStore, WildernessVisitor.EntityVisitor> entityStoreResourceType;

   public WildernessPlugin(@Nonnull JavaPluginInit init) {
      super(init);
      instance = this;
   }

   @Override
   protected void setup() {
      this.wildernessTrackerResourceType = this.getChunkStoreRegistry().registerResource(WildernessTracker.class, WildernessTracker::new);
      this.chunkStoreResourceType = this.getChunkStoreRegistry().registerResource(WildernessVisitor.ChunkVisitor.class, WildernessVisitor.ChunkVisitor::new);
      this.entityStoreResourceType = this.getEntityStoreRegistry()
         .registerResource(WildernessVisitor.EntityVisitor.class, WildernessVisitor.EntityVisitor::new);
      this.getChunkStoreRegistry().registerSystem(new WildernessTrackerSystems.EntityAddRemove());
      this.getChunkStoreRegistry().registerSystem(new WildernessTrackerSystems.ComponentAddRemove());
      this.getEventRegistry().register(LoadedAssetsEvent.class, GameplayConfig.class, WildernessPlugin::onGameplayConfigLoaded);
   }

   @Nonnull
   public WildernessTracker getTracker(@Nonnull World world) {
      return world.getChunkStore().getStore().getResource(this.wildernessTrackerResourceType);
   }

   public ResourceType<ChunkStore, WildernessTracker> getWildernessTrackerResourceType() {
      return this.wildernessTrackerResourceType;
   }

   public ResourceType<ChunkStore, WildernessVisitor.ChunkVisitor> getChunkResourceType() {
      return this.chunkStoreResourceType;
   }

   public ResourceType<EntityStore, WildernessVisitor.EntityVisitor> getEntityResourceType() {
      return this.entityStoreResourceType;
   }

   @Nonnull
   public static CompletableFuture<WildernessTracker> reloadTracker(@Nonnull World world) {
      return CompletableFuture.completedFuture(world).thenApplyAsync(WildernessTrackerSystems::reload, world);
   }

   public static WildernessPlugin get() {
      return instance;
   }

   protected static void onGameplayConfigLoaded(@Nonnull LoadedAssetsEvent<String, GameplayConfig, AssetMap<String, GameplayConfig>> event) {
      for (World world : Universe.get().getWorlds().values()) {
         String gameConfigAssetId = world.getWorldConfig().getGameplayConfig();
         if (event.getLoadedAssets().containsKey(gameConfigAssetId)) {
            reloadTracker(world);
         }
      }
   }
}
