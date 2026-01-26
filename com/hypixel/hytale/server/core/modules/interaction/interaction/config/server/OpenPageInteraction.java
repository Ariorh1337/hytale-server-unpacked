/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class OpenPageInteraction
extends SimpleInstantInteraction {
    public static final BuilderCodec<OpenPageInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OpenPageInteraction.class, OpenPageInteraction::new, SimpleInstantInteraction.CODEC).documentation("Opens a predefined page.")).appendInherited(new KeyedCodec<Page>("Page", new EnumCodec<Page>(Page.class)), (o, v) -> {
        o.page = v;
    }, o -> o.page, (o, p) -> {
        o.page = p.page;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Boolean>("CanCloseThroughInteraction", Codec.BOOLEAN), (o, v) -> {
        o.canCloseThroughInteraction = v;
    }, o -> o.canCloseThroughInteraction, (o, p) -> {
        o.canCloseThroughInteraction = p.canCloseThroughInteraction;
    }).add()).build();
    private static final Map<Page, PageUsageValidator> USAGE_VALIDATOR_MAP = new ConcurrentHashMap<Page, PageUsageValidator>();
    protected Page page;
    protected boolean canCloseThroughInteraction;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        PageUsageValidator validator = USAGE_VALIDATOR_MAP.get((Object)this.page);
        if (validator != null && !validator.canUse(ref, playerComponent, context, context.getCommandBuffer())) {
            return;
        }
        playerComponent.getPageManager().setPage(ref, store, this.page, this.canCloseThroughInteraction);
    }

    public static void registerUsageValidator(Page page, PageUsageValidator validator) {
        USAGE_VALIDATOR_MAP.put(page, validator);
    }

    @FunctionalInterface
    public static interface PageUsageValidator {
        public boolean canUse(Ref<EntityStore> var1, Player var2, InteractionContext var3, ComponentAccessor<EntityStore> var4);
    }
}

