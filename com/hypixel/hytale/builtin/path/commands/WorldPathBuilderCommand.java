/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path.commands;

import com.hypixel.hytale.builtin.path.WorldPathBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.path.WorldPath;
import com.hypixel.hytale.server.core.universe.world.path.WorldPathConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldPathBuilderCommand
extends AbstractCommandCollection {
    public WorldPathBuilderCommand() {
        super("builder", "server.commands.worldpath.builder.desc");
        this.addSubCommand(new WorldPathBuilderStopCommand());
        this.addSubCommand(new WorldPathBuilderLoadCommand());
        this.addSubCommand(new WorldPathBuilderSimulateCommand());
        this.addSubCommand(new WorldPathBuilderClearCommand());
        this.addSubCommand(new WorldPathBuilderAddCommand());
        this.addSubCommand(new WorldPathBuilderSetCommand());
        this.addSubCommand(new WorldPathBuilderGotoCommand());
        this.addSubCommand(new WorldPathBuilderRemoveCommand());
        this.addSubCommand(new WorldPathBuilderSaveCommand());
    }

    @Nonnull
    private static WorldPathBuilder createBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nullable WorldPath existing) {
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        String name = "Builder-" + String.valueOf(uuidComponent.getUuid());
        WorldPathBuilder builder = new WorldPathBuilder();
        if (existing == null) {
            builder.setPath(new WorldPath(name, new ObjectArrayList<Transform>()));
        } else {
            builder.setPath(new WorldPath(name, new ObjectArrayList<Transform>(existing.getWaypoints())));
        }
        return builder;
    }

    @Nullable
    private static WorldPathBuilder getBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        return store.getComponent(ref, WorldPathBuilder.getComponentType());
    }

    @Nonnull
    private static WorldPathBuilder getOrCreateBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        WorldPathBuilder builder = store.getComponent(ref, WorldPathBuilder.getComponentType());
        if (builder != null) {
            return builder;
        }
        return WorldPathBuilderCommand.putBuilder(ref, store, WorldPathBuilderCommand.createBuilder(ref, store, null));
    }

    @Nullable
    private static WorldPath removeBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        WorldPathBuilder worldPath = store.getComponent(ref, WorldPathBuilder.getComponentType());
        if (worldPath != null) {
            store.removeComponent(ref, WorldPathBuilder.getComponentType());
            return worldPath.getPath();
        }
        return null;
    }

    @Nonnull
    private static WorldPathBuilder putBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WorldPathBuilder builder) {
        store.putComponent(ref, WorldPathBuilder.getComponentType(), builder);
        return builder;
    }

    private static class WorldPathBuilderStopCommand
    extends AbstractPlayerCommand {
        public WorldPathBuilderStopCommand() {
            super("stop", "server.commands.worldpath.builder.stop.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            WorldPathBuilderCommand.removeBuilder(ref, store);
        }
    }

    private static class WorldPathBuilderLoadCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.worldpath.builder.load.name.desc", ArgTypes.STRING);

        public WorldPathBuilderLoadCommand() {
            super("load", "server.commands.worldpath.builder.load.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String name = (String)this.nameArg.get(context);
            WorldPath worldPath = world.getWorldPathConfig().getPath(name);
            if (worldPath == null) {
                context.sendMessage(Message.translation("server.universe.worldpath.noPathFound").param("path", name));
                return;
            }
            WorldPathBuilderCommand.putBuilder(ref, store, WorldPathBuilderCommand.createBuilder(ref, store, worldPath));
        }
    }

    private static class WorldPathBuilderSimulateCommand
    extends AbstractPlayerCommand {
        public WorldPathBuilderSimulateCommand() {
            super("simulate", "server.commands.worldpath.builder.simulate.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            ScheduledFuture[] scheduledFuture;
            WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
            if (builder == null) {
                return;
            }
            ObjectArrayList<Transform> waypoints = new ObjectArrayList<Transform>(builder.getPath().getWaypoints());
            CompletableFuture future = new CompletableFuture();
            scheduledFuture = new ScheduledFuture[]{HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
                Transform transform = (Transform)waypoints.removeFirst();
                if (transform == null) {
                    future.complete(null);
                    scheduledFuture[0].cancel(false);
                } else {
                    world.execute(() -> {
                        Teleport teleportComponent = Teleport.createForPlayer(transform);
                        store.addComponent(ref, Teleport.getComponentType(), teleportComponent);
                    });
                }
            }, 1L, 1L, TimeUnit.SECONDS)};
        }
    }

    private static class WorldPathBuilderClearCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINTS_CLEARED = Message.translation("server.universe.worldpath.pointsCleared");

        public WorldPathBuilderClearCommand() {
            super("clear", "server.commands.worldpath.builder.clear.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
            if (builder == null) {
                return;
            }
            builder.getPath().getWaypoints().clear();
            context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINTS_CLEARED);
        }
    }

    private static class WorldPathBuilderAddCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINT_ADDED = Message.translation("server.universe.worldpath.pointAdded");

        public WorldPathBuilderAddCommand() {
            super("add", "server.commands.worldpath.builder.add.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Transform transform = transformComponent.getTransform().clone();
            WorldPathBuilderCommand.getOrCreateBuilder(ref, store).getPath().getWaypoints().add(transform);
            context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINT_ADDED);
        }
    }

    private static class WorldPathBuilderSetCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINT_SET = Message.translation("server.universe.worldpath.pointSet");
        @Nonnull
        private final OptionalArg<Integer> indexArg = this.withOptionalArg("index", "server.commands.worldpath.builder.set.index.desc", ArgTypes.INTEGER);

        public WorldPathBuilderSetCommand() {
            super("set", "server.commands.worldpath.builder.set.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
            if (builder == null) {
                return;
            }
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            assert (transformComponent != null);
            WorldPath worldPath = builder.getPath();
            int index = this.indexArg.provided(context) ? (Integer)this.indexArg.get(context) : worldPath.getWaypoints().size() - 1;
            worldPath.getWaypoints().set(index, transformComponent.getTransform().clone());
            context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINT_SET);
        }
    }

    private static class WorldPathBuilderGotoCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<Integer> indexArg = this.withRequiredArg("index", "server.commands.worldpath.builder.goto.index.desc", ArgTypes.INTEGER);

        public WorldPathBuilderGotoCommand() {
            super("goto", "server.commands.worldpath.builder.goto.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
            if (builder == null) {
                return;
            }
            Integer index = (Integer)this.indexArg.get(context);
            WorldPath worldPath = builder.getPath();
            Transform waypointTransform = worldPath.getWaypoints().get(index);
            Teleport teleportComponent = Teleport.createForPlayer(null, waypointTransform);
            store.addComponent(ref, Teleport.getComponentType(), teleportComponent);
            context.sendMessage(Message.translation("server.universe.worldpath.teleportedToPoint").param("index", index));
        }
    }

    private static class WorldPathBuilderRemoveCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<Integer> indexArg = this.withRequiredArg("index", "server.commands.worldpath.builder.remove.index.desc", ArgTypes.INTEGER);

        public WorldPathBuilderRemoveCommand() {
            super("remove", "server.commands.worldpath.builder.remove.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
            if (builder == null) {
                return;
            }
            int index = (Integer)this.indexArg.get(context);
            builder.getPath().getWaypoints().remove(index);
            context.sendMessage(Message.translation("server.universe.worldpath.removedIndex").param("index", index));
        }
    }

    private static class WorldPathBuilderSaveCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private static final Message MESSAGE_UNIVERSE_WORLD_PATH_NO_POINTS_DEFINED = Message.translation("server.universe.worldpath.noPointsDefined");
        @Nonnull
        private static final Message MESSAGE_UNIVERSE_WORLD_PATH_SAVED = Message.translation("server.universe.worldpath.saved");
        @Nonnull
        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.worldpath.builder.save.name.desc", ArgTypes.STRING);

        public WorldPathBuilderSaveCommand() {
            super("save", "server.commands.worldpath.builder.save.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String name = (String)this.nameArg.get(context);
            WorldPath path = WorldPathBuilderCommand.removeBuilder(ref, store);
            if (path == null || path.getWaypoints().isEmpty()) {
                context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_NO_POINTS_DEFINED);
                return;
            }
            WorldPathConfig worldPathConfig = world.getWorldPathConfig();
            WorldPath worldPath = new WorldPath(name, path.getWaypoints());
            worldPathConfig.putPath(worldPath);
            worldPathConfig.save(world);
            context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_SAVED);
        }
    }
}

