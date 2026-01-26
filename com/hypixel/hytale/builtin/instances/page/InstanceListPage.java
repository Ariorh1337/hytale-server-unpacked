/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.page;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstanceListPage
extends InteractiveCustomUIPage<PageData> {
    private static final String COMMON_TEXT_BUTTON_DOCUMENT = "Pages/BasicTextButton.ui";
    private static final Value<String> BUTTON_LABEL_STYLE = Value.ref("Pages/BasicTextButton.ui", "LabelStyle");
    private static final Value<String> BUTTON_LABEL_STYLE_SELECTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    @Nullable
    private String selectedInstance;
    private List<String> instances = new ObjectArrayList<String>();

    public InstanceListPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PageData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/InstanceListPage.ui");
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Spawn", EventData.of("Action", Action.Spawn.toString()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Load", EventData.of("Action", Action.Load.toString()));
        commandBuilder.set("#Load.Visible", !AssetModule.get().getBaseAssetPack().isImmutable());
        int buttonIndex = 0;
        for (String instance : InstancesPlugin.get().getInstanceAssets()) {
            commandBuilder.append("#List", COMMON_TEXT_BUTTON_DOCUMENT);
            commandBuilder.set("#List[" + buttonIndex + "].Text", instance);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#List[" + buttonIndex + "]", EventData.of("Instance", instance));
            this.instances.add(instance);
            ++buttonIndex;
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageData data) {
        if (data.getInstance() != null) {
            this.updateSelection(data.getInstance());
        }
        if (data.getAction() != null) {
            switch (data.getAction().ordinal()) {
                case 1: {
                    if (this.selectedInstance == null) break;
                    this.load(ref, store);
                    this.close();
                    break;
                }
                case 2: {
                    if (this.selectedInstance == null) break;
                    this.spawn(ref, store);
                    this.close();
                }
            }
        }
    }

    private void load(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        Player player = store.getComponent(ref, Player.getComponentType());
        InstancesPlugin.get();
        ((CompletableFuture)InstancesPlugin.loadInstanceAssetForEdit(this.selectedInstance).thenAccept(world -> {
            Store playerStore = ref.getStore();
            World playerWorld = ((EntityStore)playerStore.getExternalData()).getWorld();
            playerWorld.execute(() -> {
                Transform spawn = world.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, playerStore);
                playerStore.addComponent(ref, Teleport.getComponentType(), new Teleport((World)world, spawn));
            });
        })).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void spawn(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            Transform returnLocation = new Transform(position.clone(), headRotationComponent.getRotation().clone());
            CompletableFuture<World> instanceWorld = InstancesPlugin.get().spawnInstance(this.selectedInstance, world, returnLocation);
            InstancesPlugin.teleportPlayerToLoadingInstance(ref, store, instanceWorld, null);
        });
    }

    private void updateSelection(String instance) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        if (this.selectedInstance != null) {
            commandBuilder.set("#List[" + this.instances.indexOf(this.selectedInstance) + "].Style", BUTTON_LABEL_STYLE);
        }
        this.selectedInstance = Objects.equals(instance, this.selectedInstance) ? null : instance;
        if (this.selectedInstance != null) {
            commandBuilder.set("#List[" + this.instances.indexOf(this.selectedInstance) + "].Style", BUTTON_LABEL_STYLE_SELECTED);
        }
        commandBuilder.set("#Name.Text", this.selectedInstance != null ? this.selectedInstance : "");
        commandBuilder.set("#Spawn.Disabled", this.selectedInstance == null);
        commandBuilder.set("#Load.Disabled", this.selectedInstance == null);
        this.sendUpdate(commandBuilder, false);
    }

    public static class PageData {
        public static final String KEY_INSTANCE = "Instance";
        public static final String KEY_ACTION = "Action";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).addField(new KeyedCodec<String>("Instance", BuilderCodec.STRING), (o, i) -> {
            o.instance = i;
        }, o -> o.instance)).addField(new KeyedCodec<Action>("Action", new EnumCodec<Action>(Action.class)), (o, i) -> {
            o.action = i;
        }, o -> o.action)).build();
        private String instance;
        private Action action;

        public String getInstance() {
            return this.instance;
        }

        public Action getAction() {
            return this.action;
        }
    }

    public static enum Action {
        Select,
        Load,
        Spawn;

    }
}

