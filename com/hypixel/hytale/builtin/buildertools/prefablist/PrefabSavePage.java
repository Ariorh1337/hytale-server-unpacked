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
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

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
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("Action", Action.Save.name()).append("@Name", "#NameInput.Value").append("@Entities", "#Entities #CheckBox.Value").append("@Empty", "#Empty #CheckBox.Value").append("@Overwrite", "#Overwrite #CheckBox.Value").append("@FromClipboard", "#FromClipboard #CheckBox.Value"));
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
                BuilderToolsPlugin.addToQueue(playerComponent, this.playerRef, (r, s, componentAccessor) -> {
                    if (data.fromClipboard) {
                        s.save((Ref<EntityStore>)r, data.name, true, data.overwrite, (ComponentAccessor<EntityStore>)componentAccessor);
                    } else {
                        s.saveFromSelection((Ref<EntityStore>)r, data.name, true, data.overwrite, data.entities, data.empty, (ComponentAccessor<EntityStore>)componentAccessor);
                    }
                });
                break;
            }
            case 1: {
                playerComponent.getPageManager().setPage(ref, store, Page.None);
            }
        }
    }

    protected static class PageData {
        public static final String NAME = "@Name";
        public static final String ENTITIES = "@Entities";
        public static final String EMPTY = "@Empty";
        public static final String OVERWRITE = "@Overwrite";
        public static final String FROM_CLIPBOARD = "@FromClipboard";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).append(new KeyedCodec<Action>("Action", new EnumCodec<Action>(Action.class, EnumCodec.EnumStyle.LEGACY)), (o, action) -> {
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
        }, o -> o.fromClipboard).add()).build();
        public Action action;
        public String name;
        public boolean entities = true;
        public boolean empty = false;
        public boolean overwrite = false;
        public boolean fromClipboard = false;
    }

    public static enum Action {
        Save,
        Cancel;

    }
}

