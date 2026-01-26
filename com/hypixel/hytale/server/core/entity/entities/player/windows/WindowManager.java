/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ExtraResources;
import com.hypixel.hytale.protocol.InventorySection;
import com.hypixel.hytale.protocol.packets.window.CloseWindow;
import com.hypixel.hytale.protocol.packets.window.OpenWindow;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialContainerWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ValidatedWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WindowManager {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final AtomicInteger windowId = new AtomicInteger(1);
    @Nonnull
    private final Int2ObjectConcurrentHashMap<Window> windows = new Int2ObjectConcurrentHashMap();
    @Nonnull
    private final Int2ObjectConcurrentHashMap<EventRegistration> windowChangeEvents = new Int2ObjectConcurrentHashMap();
    private PlayerRef playerRef;

    public void init(@Nonnull PlayerRef playerRef) {
        this.playerRef = playerRef;
    }

    @Nullable
    public UpdateWindow clientOpenWindow(@Nonnull Window window) {
        if (!Window.CLIENT_REQUESTABLE_WINDOW_TYPES.containsKey((Object)window.getType())) {
            throw new IllegalArgumentException("Client opened window must be registered in Window.CLIENT_REQUESTABLE_WINDOW_TYPES but got: " + String.valueOf((Object)window.getType()));
        }
        boolean id = false;
        Window oldWindow = this.windows.remove(0);
        if (oldWindow != null) {
            if (oldWindow instanceof ItemContainerWindow) {
                this.windowChangeEvents.remove(oldWindow.getId()).unregister();
            }
            oldWindow.onClose();
            LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), (Object)oldWindow.getType(), 0);
        }
        this.setWindow0(0, window);
        if (!window.onOpen()) {
            this.closeWindow(0);
            window.setId(-1);
            return null;
        }
        if (!window.consumeIsDirty()) {
            return null;
        }
        InventorySection section = null;
        if (window instanceof ItemContainerWindow) {
            section = ((ItemContainerWindow)((Object)window)).getItemContainer().toPacket();
        }
        ExtraResources extraResources = null;
        if (window instanceof MaterialContainerWindow) {
            extraResources = ((MaterialContainerWindow)((Object)window)).getExtraResourcesSection().toPacket();
        }
        return new UpdateWindow(0, window.getData().toString(), section, extraResources);
    }

    @Nullable
    public OpenWindow openWindow(@Nonnull Window window) {
        int id = this.windowId.getAndUpdate(operand -> ++operand > 0 ? operand : 1);
        this.setWindow(id, window);
        if (!window.onOpen()) {
            this.closeWindow(id);
            window.setId(-1);
            return null;
        }
        window.consumeIsDirty();
        LOGGER.at(Level.FINE).log("%s opened window %s with id %s and data %s", this.playerRef.getUuid(), (Object)window.getType(), id, window.getData());
        InventorySection section = null;
        if (window instanceof ItemContainerWindow) {
            section = ((ItemContainerWindow)((Object)window)).getItemContainer().toPacket();
        }
        ExtraResources extraResources = null;
        if (window instanceof MaterialContainerWindow) {
            extraResources = ((MaterialContainerWindow)((Object)window)).getExtraResourcesSection().toPacket();
        }
        return new OpenWindow(id, window.getType(), window.getData().toString(), section, extraResources);
    }

    @Nullable
    public List<OpenWindow> openWindows(Window ... windows) {
        ObjectArrayList<OpenWindow> packets = new ObjectArrayList<OpenWindow>();
        for (Window window : windows) {
            OpenWindow packet = this.openWindow(window);
            if (packet == null) {
                for (OpenWindow addedPacket : packets) {
                    this.closeWindow(addedPacket.id);
                }
                return null;
            }
            packets.add(packet);
        }
        return packets;
    }

    public void setWindow(int id, @Nonnull Window window) {
        if (id >= this.windowId.get()) {
            throw new IllegalArgumentException("id is outside of the range, use addWindow");
        }
        if (id == 0 || id == -1) {
            throw new IllegalArgumentException("id is invalid, can't be 0 or -1");
        }
        this.setWindow0(id, window);
    }

    private void setWindow0(int id, @Nonnull Window window) {
        if (this.windows.putIfAbsent(id, window) != null) {
            throw new IllegalArgumentException("Window " + id + " already exists");
        }
        window.setId(id);
        window.init(this.playerRef, this);
        if (window instanceof ItemContainerWindow) {
            ItemContainer itemContainer = ((ItemContainerWindow)((Object)window)).getItemContainer();
            this.windowChangeEvents.put(id, itemContainer.registerChangeEvent(EventPriority.LAST, e -> this.markWindowChanged(id)));
        }
    }

    @Nullable
    public Window getWindow(int id) {
        if (id == -1) {
            throw new IllegalArgumentException("Window id -1 is invalid!");
        }
        return this.windows.get(id);
    }

    @Nonnull
    public List<Window> getWindows() {
        return new ObjectArrayList<Window>(this.windows.values());
    }

    public void updateWindow(@Nonnull Window window) {
        MaterialContainerWindow materialContainerWindow;
        InventorySection section = null;
        if (window instanceof ItemContainerWindow) {
            ItemContainerWindow itemContainerWindow = (ItemContainerWindow)((Object)window);
            section = itemContainerWindow.getItemContainer().toPacket();
        }
        ExtraResources extraResources = null;
        if (window instanceof MaterialContainerWindow && !(materialContainerWindow = (MaterialContainerWindow)((Object)window)).isValid()) {
            extraResources = materialContainerWindow.getExtraResourcesSection().toPacket();
        }
        this.playerRef.getPacketHandler().writeNoCache(new UpdateWindow(window.getId(), window.getData().toString(), section, extraResources));
        window.consumeNeedRebuild();
        LOGGER.at(Level.FINER).log("%s update window %s with id %s and data %s", this.playerRef.getUuid(), (Object)window.getType(), window.getId(), window.getData());
    }

    @Nonnull
    public Window closeWindow(int id) {
        if (id == -1) {
            throw new IllegalArgumentException("Window id -1 is invalid!");
        }
        this.playerRef.getPacketHandler().writeNoCache(new CloseWindow(id));
        Window window = this.windows.remove(id);
        if (window instanceof ItemContainerWindow) {
            this.windowChangeEvents.remove(window.getId()).unregister();
        }
        if (window == null) {
            throw new IllegalStateException("Window id " + id + " is invalid!");
        }
        window.onClose();
        LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), (Object)window.getType(), id);
        return window;
    }

    public void closeAllWindows() {
        for (Window window : this.windows.values()) {
            window.close();
        }
    }

    public void markWindowChanged(int id) {
        Window window = this.getWindow(id);
        if (window != null) {
            window.invalidate();
        }
    }

    public void updateWindows() {
        this.windows.forEach((id, window, _windowManager) -> {
            if (window.consumeIsDirty()) {
                _windowManager.updateWindow((Window)window);
            }
        }, this);
    }

    public void validateWindows() {
        for (Window value : this.windows.values()) {
            if (!(value instanceof ValidatedWindow) || ((ValidatedWindow)((Object)value)).validate()) continue;
            value.close();
        }
    }

    public static <W extends Window> void closeAndRemoveAll(@Nonnull Map<UUID, W> windows) {
        Iterator<W> iterator = windows.values().iterator();
        while (iterator.hasNext()) {
            ((Window)iterator.next()).close();
            iterator.remove();
        }
    }

    @Nonnull
    public String toString() {
        return "WindowManager{windowId=" + String.valueOf(this.windowId) + ", windows=" + String.valueOf(this.windows) + "}";
    }
}

