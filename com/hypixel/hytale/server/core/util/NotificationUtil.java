/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.packets.interface_.Notification;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NotificationUtil {
    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable String icon, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        for (World world : Universe.get().getWorlds().values()) {
            world.execute(() -> {
                Store<EntityStore> store = world.getEntityStore().getStore();
                NotificationUtil.sendNotificationToWorld(message, secondaryMessage, icon, item, style, store);
            });
        }
    }

    public static void sendNotificationToUniverse(@Nonnull String message) {
        NotificationUtil.sendNotificationToUniverse(Message.raw(message), null, null, null, NotificationStyle.Default);
    }

    public static void sendNotificationToUniverse(@Nonnull String message, @Nonnull String secondaryMessage) {
        NotificationUtil.sendNotificationToUniverse(Message.raw(message), Message.raw(secondaryMessage), null, null, NotificationStyle.Default);
    }

    public static void sendNotificationToUniverse(@Nonnull String message, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(Message.raw(message), null, null, null, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message) {
        NotificationUtil.sendNotificationToUniverse(message, null, null, null, NotificationStyle.Default);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, null, null, null, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable String icon, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, null, icon, null, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, null, null, item, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable String icon) {
        NotificationUtil.sendNotificationToUniverse(message, secondaryMessage, icon, null, NotificationStyle.Default);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable ItemWithAllMetadata item) {
        NotificationUtil.sendNotificationToUniverse(message, secondaryMessage, null, item, NotificationStyle.Default);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, secondaryMessage, null, null, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable String icon, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, secondaryMessage, icon, null, style);
    }

    public static void sendNotificationToUniverse(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotificationToUniverse(message, secondaryMessage, null, item, style);
    }

    public static void sendNotificationToWorld(@Nonnull Message message, @Nullable Message secondaryMessage, @Nullable String icon, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style, @Nonnull Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        for (PlayerRef playerRefComponent : world.getPlayerRefs()) {
            NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), message, secondaryMessage, icon, item, style);
        }
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull Message message, @Nullable Message secondaryMessage, @Nullable String icon, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        Objects.requireNonNull(message, "Notification message can't be null!");
        Objects.requireNonNull(style, "Notification style can't be null!");
        handler.writeNoCache(new Notification(message.getFormattedMessage(), secondaryMessage != null ? secondaryMessage.getFormattedMessage() : null, icon, item, style));
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull String message) {
        NotificationUtil.sendNotification(handler, Message.raw(message), null, null, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull Message message, @Nonnull String icon) {
        NotificationUtil.sendNotification(handler, message, null, icon, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull Message message, @Nonnull String icon, @Nonnull NotificationStyle notificationStyle) {
        NotificationUtil.sendNotification(handler, message, null, icon, null, notificationStyle);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull String message, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotification(handler, Message.raw(message), null, null, null, style);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull Message message) {
        NotificationUtil.sendNotification(handler, message, null, null, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, NotificationStyle style) {
        NotificationUtil.sendNotification(handler, message, null, null, null, style);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, @Nonnull String message, @Nonnull String secondaryMessage) {
        NotificationUtil.sendNotification(handler, Message.raw(message), Message.raw(secondaryMessage), null, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage, String icon) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, icon, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, null, null, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage, ItemWithAllMetadata item) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, null, item, NotificationStyle.Default);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, null, null, style);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage, String icon, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, icon, null, style);
    }

    public static void sendNotification(@Nonnull PacketHandler handler, Message message, Message secondaryMessage, ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        NotificationUtil.sendNotification(handler, message, secondaryMessage, null, item, style);
    }
}

