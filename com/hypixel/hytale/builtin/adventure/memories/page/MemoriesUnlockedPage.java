/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.page;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.page.MemoriesPage;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class MemoriesUnlockedPage
extends InteractiveCustomUIPage<PageEventData> {
    private final BlockPosition blockPosition;

    public MemoriesUnlockedPage(@Nonnull PlayerRef playerRef, BlockPosition blockPosition) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        this.blockPosition = blockPosition;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
        commandBuilder.append("Pages/Memories/MemoriesUnlocked.ui");
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#DiscoverMemoriesButton", new EventData().append("Action", PageAction.DiscoverMemories));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert (player != null);
        if (data.action == PageAction.DiscoverMemories) {
            player.getPageManager().openCustomPage(ref, store, new MemoriesPage(this.playerRef, this.blockPosition));
        }
    }

    public static class PageEventData {
        public static final String KEY_ACTION = "Action";
        public static final BuilderCodec<PageEventData> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PageEventData.class, PageEventData::new).append(new KeyedCodec<PageAction>("Action", PageAction.CODEC), (pageEventData, pageAction) -> {
            pageEventData.action = pageAction;
        }, pageEventData -> pageEventData.action).add()).build();
        public PageAction action;
    }

    public static enum PageAction {
        DiscoverMemories;

        public static final Codec<PageAction> CODEC;

        static {
            CODEC = new EnumCodec<PageAction>(PageAction.class);
        }
    }
}

