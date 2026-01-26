/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefablist.PrefabPage;
import com.hypixel.hytale.builtin.buildertools.prefablist.PrefabSavePage;
import com.hypixel.hytale.builtin.buildertools.utils.RecursivePrefabLoader;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabCommand
extends AbstractCommandCollection {
    public PrefabCommand() {
        super("prefab", "server.commands.prefab.desc");
        this.addAliases("p");
        this.setPermissionGroup(GameMode.Creative);
        this.addSubCommand(new PrefabSaveCommand());
        this.addSubCommand(new PrefabLoadCommand());
        this.addSubCommand(new PrefabDeleteCommand());
        this.addSubCommand(new PrefabListCommand());
    }

    private static class PrefabSaveCommand
    extends AbstractPlayerCommand {
        public PrefabSaveCommand() {
            super("save", "server.commands.prefab.save.desc");
            this.requirePermission("hytale.editor.prefab.manage");
            this.addUsageVariant(new PrefabSaveDirectCommand());
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            playerComponent.getPageManager().openCustomPage(ref, store, new PrefabSavePage(playerRef));
        }
    }

    private static class PrefabLoadCommand
    extends AbstractPlayerCommand {
        public PrefabLoadCommand() {
            super("load", "server.commands.prefab.load.desc");
            this.requirePermission("hytale.editor.prefab.use");
            this.addUsageVariant(new PrefabLoadByNameCommand());
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            List<PrefabStore.AssetPackPrefabPath> assetPaths = PrefabStore.get().getAllAssetPrefabPaths();
            Path defaultRoot = assetPaths.isEmpty() ? PrefabStore.get().getServerPrefabsPath() : assetPaths.getFirst().prefabsPath();
            BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
            playerComponent.getPageManager().openCustomPage(ref, store, new PrefabPage(playerRef, defaultRoot, builderState));
        }
    }

    private static class PrefabDeleteCommand
    extends CommandBase {
        @Nonnull
        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.prefab.delete.name.desc", ArgTypes.STRING);

        public PrefabDeleteCommand() {
            super("delete", "server.commands.prefab.delete.desc", true);
            this.requirePermission("hytale.editor.prefab.manage");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Object name = (String)this.nameArg.get(context);
            if (!((String)name).endsWith(".prefab.json")) {
                name = (String)name + ".prefab.json";
            }
            PrefabStore module = PrefabStore.get();
            Path serverPrefabsPath = module.getServerPrefabsPath();
            Path resolve = serverPrefabsPath.resolve((String)name);
            try {
                Store<EntityStore> store;
                PlayerRef playerRefComponent;
                Ref<EntityStore> ref = context.senderAsPlayerRef();
                boolean isOwner = false;
                if (ref != null && ref.isValid() && (playerRefComponent = (store = ref.getStore()).getComponent(ref, PlayerRef.getComponentType())) != null) {
                    isOwner = SingleplayerModule.isOwner(playerRefComponent);
                }
                if (!PathUtil.isChildOf(serverPrefabsPath, resolve) && !isOwner) {
                    context.sendMessage(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"));
                    return;
                }
                Path relativize = PathUtil.relativize(serverPrefabsPath, resolve);
                if (Files.isRegularFile(resolve, new LinkOption[0])) {
                    Files.delete(resolve);
                    context.sendMessage(Message.translation("server.builderTools.prefab.deleted").param("name", relativize.toString()));
                } else {
                    context.sendMessage(Message.translation("server.builderTools.prefab.prefabNotFound").param("name", relativize.toString()));
                }
            }
            catch (IOException e) {
                context.sendMessage(Message.translation("server.builderTools.prefab.errorOccured").param("reason", e.getMessage()));
            }
        }
    }

    private static class PrefabListCommand
    extends CommandBase {
        @Nonnull
        private final DefaultArg<String> storeTypeArg = this.withDefaultArg("storeType", "server.commands.prefab.list.storeType.desc", ArgTypes.STRING, "asset", "server.commands.prefab.list.storeType.desc");
        @Nonnull
        private final FlagArg textFlag = this.withFlagArg("text", "server.commands.prefab.list.text.desc");

        public PrefabListCommand() {
            super("list", "server.commands.prefab.list.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            String storeType;
            Path prefabStorePath = switch (storeType = (String)this.storeTypeArg.get(context)) {
                case "server" -> PrefabStore.get().getServerPrefabsPath();
                case "asset" -> {
                    List<PrefabStore.AssetPackPrefabPath> assetPaths = PrefabStore.get().getAllAssetPrefabPaths();
                    if (assetPaths.isEmpty()) {
                        yield PrefabStore.get().getAssetPrefabsPath();
                    }
                    yield assetPaths.getFirst().prefabsPath();
                }
                case "worldgen" -> PrefabStore.get().getWorldGenPrefabsPath();
                default -> throw new IllegalStateException("Unexpected value: " + storeType);
            };
            Ref<EntityStore> ref = context.senderAsPlayerRef();
            if (ref != null && ref.isValid() && !((Boolean)this.textFlag.get(context)).booleanValue()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                Path finalPrefabStorePath = prefabStorePath;
                world.execute(() -> {
                    Player playerComponent = store.getComponent(ref, Player.getComponentType());
                    assert (playerComponent != null);
                    PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                    assert (playerRefComponent != null);
                    BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRefComponent);
                    playerComponent.getPageManager().openCustomPage(ref, store, new PrefabPage(playerRefComponent, finalPrefabStorePath, builderState));
                });
                return;
            }
            try {
                final ObjectArrayList<Message> prefabFiles = new ObjectArrayList<Message>();
                if ("asset".equals(storeType)) {
                    for (PrefabStore.AssetPackPrefabPath packPath : PrefabStore.get().getAllAssetPrefabPaths()) {
                        String packPrefix;
                        final Path path = packPath.prefabsPath();
                        String string = packPrefix = packPath.isBasePack() ? "" : "[" + packPath.getPackName() + "] ";
                        if (!Files.isDirectory(path, new LinkOption[0])) continue;
                        Files.walkFileTree(path, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){

                            @Override
                            @Nonnull
                            public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                                String fileName = file.getFileName().toString();
                                if (fileName.endsWith(".prefab.json")) {
                                    prefabFiles.add(Message.raw(packPrefix + PathUtil.relativize(path, file).toString()));
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                } else {
                    final Path path = prefabStorePath;
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        Files.walkFileTree(path, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){

                            @Override
                            @Nonnull
                            public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                                String fileName = file.getFileName().toString();
                                if (fileName.endsWith(".prefab.json")) {
                                    prefabFiles.add(Message.raw(PathUtil.relativize(path, file).toString()));
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                }
                context.sendMessage(MessageFormat.list(Message.translation("server.commands.prefab.list.header"), prefabFiles));
            }
            catch (IOException e) {
                context.sendMessage(Message.translation("server.builderTools.prefab.errorListingPrefabs").param("reason", e.getMessage()));
            }
        }
    }

    private static class PrefabLoadByNameCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.prefab.load.name.desc", ArgTypes.STRING);
        @Nonnull
        private final DefaultArg<String> storeTypeArg = this.withDefaultArg("storeType", "server.commands.prefab.load.storeType.desc", ArgTypes.STRING, "asset", "server.commands.prefab.load.storeType.desc");
        @Nonnull
        private final DefaultArg<String> storeNameArg = this.withDefaultArg("storeName", "server.commands.prefab.load.storeName.desc", ArgTypes.STRING, null, "");
        @Nonnull
        private final FlagArg childrenFlag = this.withFlagArg("children", "server.commands.prefab.load.children.desc");

        public PrefabLoadByNameCommand() {
            super("server.commands.prefab.load.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            boolean prefabExists;
            Function<String, BlockSelection> prefabGetter;
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            String storeType = (String)this.storeTypeArg.get(context);
            String storeName = (String)this.storeNameArg.get(context);
            Object name = (String)this.nameArg.get(context);
            if (!((String)name).endsWith(".prefab.json")) {
                name = (String)name + ".prefab.json";
            }
            Path prefabStorePath = null;
            Path resolvedPrefabPath = null;
            Object finalName = name;
            switch (storeType) {
                case "server": {
                    prefabStorePath = PrefabStore.get().getServerPrefabsPath();
                    Function<String, BlockSelection> function = PrefabStore.get()::getServerPrefab;
                    break;
                }
                case "asset": {
                    Function<String, BlockSelection> function;
                    Path foundPath = PrefabStore.get().findAssetPrefabPath((String)finalName);
                    if (foundPath != null) {
                        resolvedPrefabPath = foundPath;
                        prefabStorePath = foundPath.getParent();
                        function = key -> PrefabStore.get().getPrefab(foundPath);
                        break;
                    }
                    prefabStorePath = PrefabStore.get().getAssetPrefabsPath();
                    function = PrefabStore.get()::getAssetPrefab;
                    break;
                }
                case "worldgen": {
                    Path storePath = PrefabStore.get().getWorldGenPrefabsPath(storeName);
                    prefabStorePath = PrefabStore.get().getWorldGenPrefabsPath(storeName);
                    Function<String, BlockSelection> function = key -> PrefabStore.get().getWorldGenPrefab(storePath, (String)key);
                    break;
                }
                default: {
                    context.sendMessage(Message.translation("server.commands.prefab.invalidStoreType").param("storeType", storeType));
                    Function<String, BlockSelection> function = prefabGetter = null;
                }
            }
            if (prefabGetter == null) {
                return;
            }
            Path finalPrefabStorePath = prefabStorePath;
            RecursivePrefabLoader.BlockSelectionLoader loader = (Boolean)this.childrenFlag.get(context) != false ? new RecursivePrefabLoader.BlockSelectionLoader(finalPrefabStorePath, prefabGetter) : (prefabFile, rand) -> (BlockSelection)prefabGetter.apply((String)prefabFile);
            boolean bl = prefabExists = resolvedPrefabPath != null && Files.isRegularFile(resolvedPrefabPath, new LinkOption[0]) || Files.isRegularFile(prefabStorePath.resolve((String)name), new LinkOption[0]);
            if (prefabExists) {
                BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (arg_0, arg_1, arg_2) -> PrefabLoadByNameCommand.lambda$execute$3((String)finalName, loader, arg_0, arg_1, arg_2));
            } else {
                context.sendMessage(Message.translation("server.builderTools.prefab.prefabNotFound").param("name", (String)name));
            }
        }

        private static /* synthetic */ void lambda$execute$3(String finalName, BiFunction loader, Ref r, BuilderToolsPlugin.BuilderState s, ComponentAccessor componentAccessor) throws RuntimeException {
            s.load(finalName, (BlockSelection)loader.apply(finalName, s.getRandom()), componentAccessor);
        }
    }

    private static class PrefabSaveDirectCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.prefab.save.name.desc", ArgTypes.STRING);
        @Nonnull
        private final FlagArg overwriteFlag = this.withFlagArg("overwrite", "server.commands.prefab.save.overwrite.desc");
        @Nonnull
        private final FlagArg entitiesFlag = this.withFlagArg("entities", "server.commands.prefab.save.entities.desc");
        @Nonnull
        private final FlagArg emptyFlag = this.withFlagArg("empty", "server.commands.prefab.save.empty.desc");
        @Nonnull
        private final FlagArg playerAnchorFlag = this.withFlagArg("playerAnchor", "server.commands.prefab.save.playerAnchor.desc");

        public PrefabSaveDirectCommand() {
            super("server.commands.prefab.save.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            String name = (String)this.nameArg.get(context);
            boolean overwrite = (Boolean)this.overwriteFlag.get(context);
            boolean entities = (Boolean)this.entitiesFlag.get(context);
            boolean empty = (Boolean)this.emptyFlag.get(context);
            Vector3i playerAnchor = this.getPlayerAnchor(ref, store, (Boolean)this.playerAnchorFlag.get(context));
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.saveFromSelection((Ref<EntityStore>)r, name, true, overwrite, entities, empty, playerAnchor, (ComponentAccessor<EntityStore>)componentAccessor));
        }

        @Nullable
        private Vector3i getPlayerAnchor(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, boolean usePlayerAnchor) {
            if (!usePlayerAnchor) {
                return null;
            }
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            if (transformComponent == null) {
                return null;
            }
            Vector3d position = transformComponent.getPosition();
            return new Vector3i(MathUtil.floor(position.getX()), MathUtil.floor(position.getY()), MathUtil.floor(position.getZ()));
        }
    }
}

