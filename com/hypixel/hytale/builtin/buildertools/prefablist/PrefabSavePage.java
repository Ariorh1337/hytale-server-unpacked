/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefablist;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabSavePage
extends InteractiveCustomUIPage<PageData> {
    @Nonnull
    private static final Message MESSAGE_SERVER_BUILDER_TOOLS_PREFAB_SAVE_NAME_REQUIRED = Message.translation("server.builderTools.prefabSave.nameRequired");

    public PrefabSavePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/PrefabSavePage.ui");
        commandBuilder.set("#Entities #CheckBox.Value", true);
        commandBuilder.set("#Empty #CheckBox.Value", false);
        commandBuilder.set("#Overwrite #CheckBox.Value", false);
        commandBuilder.set("#FromClipboard #CheckBox.Value", false);
        commandBuilder.set("#UsePlayerAnchor #CheckBox.Value", false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("Action", Action.Save.name()).append("@Name", "#NameInput.Value").append("@Entities", "#Entities #CheckBox.Value").append("@Empty", "#Empty #CheckBox.Value").append("@Overwrite", "#Overwrite #CheckBox.Value").append("@FromClipboard", "#FromClipboard #CheckBox.Value").append("@UsePlayerAnchor", "#UsePlayerAnchor #CheckBox.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", new EventData().append("Action", Action.Cancel.name()));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageData data) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        switch (data.action.ordinal()) {
            case 0: {
                if (data.name == null || data.name.isBlank()) {
                    this.playerRef.sendMessage(MESSAGE_SERVER_BUILDER_TOOLS_PREFAB_SAVE_NAME_REQUIRED);
                    this.sendUpdate();
                    return;
                }
                playerComponent.getPageManager().setPage(ref, store, Page.None);
                Vector3i playerAnchor = this.getPlayerAnchor(ref, store, data.usePlayerAnchor && !data.fromClipboard);
                BuilderToolsPlugin.addToQueue(playerComponent, this.playerRef, (r, s, componentAccessor) -> {
                    if (data.fromClipboard) {
                        s.save((Ref<EntityStore>)r, data.name, true, data.overwrite, (ComponentAccessor<EntityStore>)componentAccessor);
                    } else {
                        s.saveFromSelection((Ref<EntityStore>)r, data.name, true, data.overwrite, data.entities, data.empty, playerAnchor, (ComponentAccessor<EntityStore>)componentAccessor);
                    }
                });
                break;
            }
            case 1: {
                playerComponent.getPageManager().setPage(ref, store, Page.None);
            }
        }
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

    protected static class PageData {
        public static final String NAME = "@Name";
        public static final String ENTITIES = "@Entities";
        public static final String EMPTY = "@Empty";
        public static final String OVERWRITE = "@Overwrite";
        public static final String FROM_CLIPBOARD = "@FromClipboard";
        public static final String USE_PLAYER_ANCHOR = "@UsePlayerAnchor";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).append(new KeyedCodec<Action>("Action", new EnumCodec<Action>(Action.class, EnumCodec.EnumStyle.LEGACY)), (o, action) -> {
            o.action = action;
        }, o -> o.action).add()).append(new KeyedCodec<String>("@Name", Codec.STRING), (o, name) -> {
            o.name = name;
        }, o -> o.name).add()).append(new KeyedCodec<Boolean>("@Entities", Codec.BOOLEAN), (o, entities) -> {
            o.entities = entities;
        }, o -> o.entities).add()).append(new KeyedCodec<Boolean>("@Empty", Codec.BOOLEAN), (o, empty) -> {
            o.empty = empty;
        }, o -> o.empty).add()).append(new KeyedCodec<Boolean>("@Overwrite", Codec.BOOLEAN), (o, overwrite) -> {
            o.overwrite = overwrite;
        }, o -> o.overwrite).add()).append(new KeyedCodec<Boolean>("@FromClipboard", Codec.BOOLEAN), (o, fromClipboard) -> {
            o.fromClipboard = fromClipboard;
        }, o -> o.fromClipboard).add()).append(new KeyedCodec<Boolean>("@UsePlayerAnchor", Codec.BOOLEAN), (o, usePlayerAnchor) -> {
            o.usePlayerAnchor = usePlayerAnchor;
        }, o -> o.usePlayerAnchor).add()).build();
        public Action action;
        public String name;
        public boolean entities = true;
        public boolean empty = false;
        public boolean overwrite = false;
        public boolean fromClipboard = false;
        public boolean usePlayerAnchor = false;
    }

    public static enum Action {
        Save,
        Cancel;

    }
}

