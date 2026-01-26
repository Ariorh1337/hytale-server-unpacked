/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.simple;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class SendMessageInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<SendMessageInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SendMessageInteraction.class, SendMessageInteraction::new, SimpleInstantInteraction.CODEC).documentation("Debug interaction that sends a message on use.")).appendInherited(new KeyedCodec<String>("Message", Codec.STRING), (interaction, s) -> {
        interaction.message = s;
    }, interaction -> interaction.message, (interaction, parent) -> {
        interaction.message = parent.message;
    }).add()).appendInherited(new KeyedCodec<String>("Key", Codec.STRING), (o, v) -> {
        o.key = v;
    }, o -> o.key, (o, p) -> {
        o.key = p.key;
    }).add()).build();
    private String key;
    private String message;

    public SendMessageInteraction(@Nonnull String id, @Nonnull String message) {
        super(id);
        this.message = message;
        this.unknown = true;
    }

    public SendMessageInteraction() {
    }

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getOwningEntity();
        Entity entity = EntityUtils.getEntity(ref, commandBuffer);
        if (entity instanceof IMessageReceiver) {
            IMessageReceiver messageReceiver = (IMessageReceiver)((Object)entity);
            if (this.key != null) {
                messageReceiver.sendMessage(Message.translation(this.key));
            } else {
                messageReceiver.sendMessage(Message.raw(this.message));
            }
        } else {
            HytaleLogger.getLogger().at(Level.INFO).log("SendMessageInteraction: %s for %s", (Object)(this.message != null ? this.message : this.key), (Object)entity);
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "SendMessageInteraction{message=" + this.message + "} " + super.toString();
    }
}

