package com.hypixel.hytale.builtin.reactiveblocks;

import com.hypixel.hytale.builtin.reactiveblocks.states.BlockExplosive;
import com.hypixel.hytale.builtin.reactiveblocks.systems.ExplosiveSystems;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class ReactiveBlocksPlugin extends JavaPlugin {
   protected static ReactiveBlocksPlugin instance;
   private ComponentType<ChunkStore, BlockExplosive> explosiveComponentType;

   public static ReactiveBlocksPlugin get() {
      return instance;
   }

   public ReactiveBlocksPlugin(@Nonnull JavaPluginInit init) {
      super(init);
      instance = this;
   }

   @Override
   protected void setup() {
      ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = this.getChunkStoreRegistry();
      ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType = BlockModule.BlockStateInfo.getComponentType();
      this.explosiveComponentType = chunkStoreRegistry.registerComponent(BlockExplosive.class, "BlockExplosive", BlockExplosive.CODEC);
      chunkStoreRegistry.registerSystem(new ExplosiveSystems.OnExplosiveAdded(blockStateInfoComponentType));
      chunkStoreRegistry.registerSystem(new ExplosiveSystems.Ticking());
   }

   public ComponentType<ChunkStore, BlockExplosive> getExplosiveComponentType() {
      return this.explosiveComponentType;
   }
}
