package com.hypixel.hytale.server.core.command.commands.world.worldgen;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.events.WorldGenChunksClearedEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.component.ChunkSavingSystems;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class WorldGenReloadCommand extends AbstractAsyncWorldCommand {
   private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_STARTED = Message.translation("server.commands.worldgen.reload.started");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_COMPLETE = Message.translation("server.commands.worldgen.reload.complete");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_DISABLED = Message.translation(
      "server.commands.worldgen.reload.chunkSavingDisabled"
   );
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_DELETING_CHUNKS = Message.translation("server.commands.worldgen.reload.deletingChunks");
   @Nonnull
   private static final Message MKESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_ENABLED = Message.translation(
      "server.commands.worldgen.reload.chunkSavingEnabled"
   );
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_REGENERATING_LOADED_CHUNKS = Message.translation(
      "server.commands.worldgen.reload.regeneratingLoadedChunks"
   );
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_ENABLED = Message.translation(
      "server.commands.worldgen.reload.chunkSavingEnabled"
   );
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_GEN_RELOAD_ALREADY_IN_PROGRESS = Message.translation("server.commands.worldgen.reload.alreadyInProgress");
   @Nonnull
   public static final Message MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ABORT = Message.translation("server.commands.worldgen.reload.abort");
   @Nonnull
   private final FlagArg clearArg = this.withFlagArg("clear", "server.commands.worldgen.reload.clear.desc");

   public WorldGenReloadCommand() {
      super("reload", "server.commands.worldgen.reload.desc");
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context, @Nonnull World world) {
      if (IS_RUNNING.get()) {
         context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_ALREADY_IN_PROGRESS);
         return CompletableFuture.completedFuture(null);
      }

      context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_STARTED);
      WorldConfig worldConfig = world.getWorldConfig();
      ChunkStore chunkComponentStore = world.getChunkStore();
      if (IS_RUNNING.getAndSet(true)) {
         context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_BENCHMARK_ABORT);
         return CompletableFuture.completedFuture(null);
      }

      try {
         IWorldGen worldGen = worldConfig.getWorldGenProvider().getGenerator();
         chunkComponentStore.setGenerator(worldGen);
         worldConfig.setDefaultSpawnProvider(worldGen);
         worldConfig.markChanged();
         IWorldMap worldMap = worldConfig.getWorldMapProvider().getGenerator(world);
         world.getWorldMapManager().setGenerator(worldMap);
         context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_COMPLETE);
      } catch (WorldGenLoadException e) {
         context.sendMessage(Message.translation("server.commands.worldgen.reload.failed").param("error", e.getTraceMessage("\n")));
         HytaleLogger.getLogger().at(Level.SEVERE).withCause(new SkipSentryException(e)).log("Failed to load WorldGen!");
         return CompletableFuture.completedFuture(null);
      } catch (Exception e) {
         context.sendMessage(Message.translation("server.commands.worldgen.reload.failed").param("error", e.getMessage()));
         HytaleLogger.getLogger().at(Level.SEVERE).withCause(e).log("Exception when trying to load WorldGen!");
         return CompletableFuture.completedFuture(null);
      } finally {
         IS_RUNNING.set(false);
      }

      return this.clearArg.provided(context) ? clearChunks(context, world) : CompletableFuture.completedFuture(null);
   }

   @Nonnull
   private static CompletableFuture<Void> clearChunks(@Nonnull CommandContext context, @Nonnull World world) {
      ChunkStore chunkComponentStore = world.getChunkStore();
      Store<ChunkStore> componentStore = chunkComponentStore.getStore();
      world.lockSaving();
      ChunkSavingSystems.Data data = componentStore.getResource(ChunkStore.SAVE_RESOURCE);
      data.clearSaveQueue();
      context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_DISABLED);
      context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_DELETING_CHUNKS);
      IChunkSaver saver = chunkComponentStore.getSaver();
      if (saver == null) {
         context.sendMessage(MKESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_ENABLED);
         return CompletableFuture.completedFuture(null);
      } else {
         return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
               try {
                  return saver.getIndexes();
               } catch (IOException e) {
                  HytaleLogger.getLogger().at(Level.SEVERE).withCause(e).log("Failed to get chunk indexes for clearing!");
                  context.sendMessage(Message.translation("server.commands.worldgen.reload.failed").param("error", e.getMessage()));
                  throw SneakyThrow.sneakyThrow(e);
               }
            }), world)
            .thenComposeAsync(
               indexes -> {
                  AtomicInteger counter = new AtomicInteger();
                  double total = indexes.size();
                  ObjectArrayList<CompletableFuture<Void>> futures = new ObjectArrayList<>();
                  LongListIterator iterator = indexes.iterator();

                  while (iterator.hasNext()) {
                     long index = iterator.nextLong();
                     int x = ChunkUtil.xOfChunkIndex(index);
                     int z = ChunkUtil.zOfChunkIndex(index);
                     futures.add(
                        saver.removeHolder(x, z)
                           .thenRun(
                              () -> {
                                 int i = counter.getAndIncrement();
                                 if (i > 0 && i % 64 == 0) {
                                    world.execute(
                                       () -> context.sendMessage(
                                          Message.translation("server.commands.worldgen.reload.deletingChunksProgress")
                                             .param("progress", MathUtil.round(i * 100 / total, 2))
                                       )
                                    );
                                 }
                              }
                           )
                     );
                  }

                  return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
               },
               world
            )
            .thenComposeAsync(
               aVoid -> {
                  context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_REGENERATING_LOADED_CHUNKS);
                  IEventDispatcher<WorldGenChunksClearedEvent, WorldGenChunksClearedEvent> clearedDispatcher = HytaleServer.get()
                     .getEventBus()
                     .dispatchFor(WorldGenChunksClearedEvent.class, world.getName());
                  if (clearedDispatcher.hasListener()) {
                     clearedDispatcher.dispatch(new WorldGenChunksClearedEvent(world));
                  }

                  LongSet chunkIndexes = chunkComponentStore.getChunkIndexes();
                  ObjectArrayList<CompletableFuture<?>> regenerateFutures = new ObjectArrayList<>();
                  LongIterator chunkIterator = chunkIndexes.iterator();

                  while (chunkIterator.hasNext()) {
                     long index = chunkIterator.nextLong();
                     regenerateFutures.add(chunkComponentStore.getChunkReferenceAsync(index, 9));
                  }

                  return CompletableFuture.allOf(regenerateFutures.toArray(CompletableFuture[]::new));
               },
               world
            )
            .thenRunAsync(() -> {
               world.unlockSaving();
               context.sendMessage(MESSAGE_COMMANDS_WORLD_GEN_RELOAD_CHUNK_SAVING_ENABLED);
            }, world);
      }
   }
}
