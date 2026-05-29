package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenCustomUIInteraction extends SimpleInstantInteraction {
   @Nonnull
   public static final CodecMapCodec<OpenCustomUIInteraction.CustomPageSupplier> PAGE_CODEC = new CodecMapCodec<>();
   @Nonnull
   public static final BuilderCodec<OpenCustomUIInteraction> CODEC = BuilderCodec.builder(
         OpenCustomUIInteraction.class, OpenCustomUIInteraction::new, SimpleInstantInteraction.CODEC
      )
      .documentation("Opens a custom ui page.")
      .<OpenCustomUIInteraction.CustomPageSupplier>appendInherited(
         new KeyedCodec<>("Page", PAGE_CODEC),
         (o, v) -> o.customPageSupplier = v,
         o -> o.customPageSupplier,
         (o, p) -> o.customPageSupplier = p.customPageSupplier
      )
      .addValidator(Validators.nonNull())
      .add()
      .build();
   private OpenCustomUIInteraction.CustomPageSupplier customPageSupplier;

   @Override
   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
      CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
      assert commandBuffer != null;
      Ref<EntityStore> ref = context.getEntity();
      Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
      if (playerComponent != null) {
         PageManager pageManager = playerComponent.getPageManager();
         if (pageManager.getCustomPage() == null) {
            PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            if (playerRefComponent != null) {
               CustomUIPage page = this.customPageSupplier.tryCreate(ref, commandBuffer, playerRefComponent, context);
               if (page != null) {
                  Store<EntityStore> store = commandBuffer.getStore();
                  pageManager.openCustomPage(ref, store, page);
               }
            }
         }
      }
   }

   public static <S extends OpenCustomUIInteraction.CustomPageSupplier> void registerCustomPageSupplier(
      @Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull S supplier
   ) {
      plugin.getCodecRegistry(PAGE_CODEC)
         .register(
            id,
            (Class<? extends OpenCustomUIInteraction.CustomPageSupplier>)supplier.getClass(),
            (Codec<? extends OpenCustomUIInteraction.CustomPageSupplier>)BuilderCodec.builder(tClass, () -> supplier).build()
         );
   }

   public static void registerSimple(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull Function<PlayerRef, CustomUIPage> supplier) {
      registerCustomPageSupplier(plugin, tClass, id, (ref, componentAccessor, playerRef, context) -> supplier.apply(playerRef));
   }

   public static void registerBlockEntityCustomPage(
      @Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull OpenCustomUIInteraction.BlockEntityCustomPageSupplier blockSupplier
   ) {
      OpenCustomUIInteraction.CustomPageSupplier supplier = (ref, var2x, playerRef, context) -> {
         BlockPosition targetBlock = context.getTargetBlock();
         if (targetBlock == null) {
            return null;
         }

         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         ChunkStore chunkStore = world.getChunkStore();
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
         Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
         if (chunkRef != null && chunkRef.isValid()) {
            WorldChunk worldChunkComponent = chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType());
            if (worldChunkComponent == null) {
               return null;
            }

            BlockPosition targetBaseBlock = world.getBaseBlock(targetBlock);
            Ref<ChunkStore> blockEntityRef = worldChunkComponent.getBlockComponentEntity(targetBaseBlock.x, targetBaseBlock.y, targetBaseBlock.z);
            return blockEntityRef != null && blockEntityRef.isValid() ? blockSupplier.tryCreate(playerRef, blockEntityRef) : null;
         } else {
            return null;
         }
      };
      registerCustomPageSupplier(plugin, tClass, id, supplier);
   }

   public static void registerBlockEntityCustomPage(
      @Nonnull PluginBase plugin,
      Class<?> tClass,
      String id,
      @Nonnull OpenCustomUIInteraction.BlockEntityCustomPageSupplier blockSupplier,
      Supplier<Holder<ChunkStore>> creator
   ) {
      OpenCustomUIInteraction.CustomPageSupplier supplier = (ref, var3x, playerRef, context) -> {
         BlockPosition targetBlock = context.getTargetBlock();
         if (targetBlock == null) {
            return null;
         }

         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         ChunkStore chunkStore = world.getChunkStore();
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
         Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
         if (chunkRef != null && chunkRef.isValid()) {
            WorldChunk worldChunk = chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType());
            if (worldChunk == null) {
               return null;
            }

            BlockPosition targetBaseBlock = world.getBaseBlock(targetBlock);
            BlockComponentChunk blockComponentChunk = worldChunk.getBlockComponentChunk();
            int index = ChunkUtil.indexBlockInColumn(targetBaseBlock.x, targetBaseBlock.y, targetBaseBlock.z);
            Ref<ChunkStore> blockEntityRef = blockComponentChunk.getEntityReference(index);
            if (blockEntityRef == null || !blockEntityRef.isValid()) {
               Holder<ChunkStore> holder = creator.get();
               holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(index, chunkRef));
               blockEntityRef = chunkComponentStore.addEntity(holder, AddReason.SPAWN);
            }

            return blockSupplier.tryCreate(playerRef, blockEntityRef);
         } else {
            return null;
         }
      };
      registerCustomPageSupplier(plugin, tClass, id, supplier);
   }

   @FunctionalInterface
   public interface BlockEntityCustomPageSupplier {
      CustomUIPage tryCreate(@Nonnull PlayerRef var1, @Nonnull Ref<ChunkStore> var2);
   }

   @FunctionalInterface
   public interface CustomPageSupplier {
      @Nullable
      CustomUIPage tryCreate(
         @Nonnull Ref<EntityStore> var1, @Nonnull ComponentAccessor<EntityStore> var2, @Nonnull PlayerRef var3, @Nonnull InteractionContext var4
      );
   }
}
