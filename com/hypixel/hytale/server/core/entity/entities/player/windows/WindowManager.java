/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
    private final Int2ObjectConcurrentHashMap<EventRegistration<?, ?>> windowChangeEvents = new Int2ObjectConcurrentHashMap();
    private PlayerRef playerRef;

    public void init(@Nonnull PlayerRef playerRef) {
        this.playerRef = playerRef;
    }

    @Nullable
    public UpdateWindow clientOpenWindow(@Nonnull Ref<EntityStore> ref, @Nonnull Window window, @Nonnull Store<EntityStore> store) {
        if (!Window.CLIENT_REQUESTABLE_WINDOW_TYPES.containsKey((Object)window.getType())) {
            throw new IllegalArgumentException("Client opened window must be registered in Window.CLIENT_REQUESTABLE_WINDOW_TYPES but got: " + String.valueOf((Object)window.getType()));
        }
        boolean id = false;
        Window oldWindow = this.windows.remove(0);
        if (oldWindow != null) {
            if (oldWindow instanceof ItemContainerWindow) {
                this.windowChangeEvents.remove(oldWindow.getId()).unregister();
            }
            oldWindow.onClose(ref, store);
            LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), (Object)oldWindow.getType(), 0);
        }
        this.setWindow0(0, window);
        if (!window.onOpen(ref, store)) {
            this.closeWindow(ref, 0, store);
            window.setId(-1);
            return null;
        }
        if (!window.consumeIsDirty()) {
            return null;
        }
        InventorySection section = null;
        if (window instanceof ItemContainerWindow) {
            ItemContainerWindow itemContainerWindow = (ItemContainerWindow)((Object)window);
            section = itemContainerWindow.getItemContainer().toPacket();
        }
        ExtraResources extraResources = null;
        if (window instanceof MaterialContainerWindow) {
            MaterialContainerWindow materialContainerWindow = (MaterialContainerWindow)((Object)window);
            extraResources = materialContainerWindow.getExtraResourcesSection().toPacket();
        }
        return new UpdateWindow(0, window.getData().toString(), section, extraResources);
    }

    @Nullable
    public OpenWindow openWindow(@Nonnull Ref<EntityStore> ref, @Nonnull Window window, @Nonnull Store<EntityStore> store) {
        int id = this.windowId.getAndUpdate(operand -> ++operand > 0 ? operand : 1);
        this.setWindow(id, window);
        if (!window.onOpen(ref, store)) {
            this.closeWindow(ref, id, store);
            window.setId(-1);
            return null;
        }
        window.consumeIsDirty();
        LOGGER.at(Level.FINE).log("%s opened window %s with id %s and data %s", this.playerRef.getUuid(), (Object)window.getType(), id, window.getData());
        InventorySection section = null;
        if (window instanceof ItemContainerWindow) {
            ItemContainerWindow itemContainerWindow = (ItemContainerWindow)((Object)window);
            section = itemContainerWindow.getItemContainer().toPacket();
        }
        ExtraResources extraResources = null;
        if (window instanceof MaterialContainerWindow) {
            MaterialContainerWindow materialContainerWindow = (MaterialContainerWindow)((Object)window);
            extraResources = materialContainerWindow.getExtraResourcesSection().toPacket();
        }
        return new OpenWindow(id, window.getType(), window.getData().toString(), section, extraResources);
    }

    @Nullable
    public List<OpenWindow> openWindows(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Window ... windows) {
        ObjectArrayList<OpenWindow> packets = new ObjectArrayList<OpenWindow>();
        for (Window window : windows) {
            OpenWindow packet = this.openWindow(ref, window, store);
            if (packet == null) {
                for (OpenWindow addedPacket : packets) {
                    this.closeWindow(ref, addedPacket.id, store);
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
            ItemContainerWindow itemContainerWindow = (ItemContainerWindow)((Object)window);
            ItemContainer itemContainer = itemContainerWindow.getItemContainer();
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
    public Window closeWindow(@Nonnull Ref<EntityStore> ref, int id, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (id == -1) {
            throw new IllegalArgumentException("Window id -1 is invalid!");
        }
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        playerRefComponent.getPacketHandler().writeNoCache(new CloseWindow(id));
        Window window = this.windows.remove(id);
        if (window instanceof ItemContainerWindow) {
            this.windowChangeEvents.remove(window.getId()).unregister();
        }
        if (window == null) {
            throw new IllegalStateException("Window id " + id + " is invalid!");
        }
        window.onClose(ref, componentAccessor);
        LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), (Object)window.getType(), id);
        return window;
    }

    public void closeAllWindows(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        for (Window window : this.windows.values()) {
            window.close(ref, componentAccessor);
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

    public void validateWindows(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        for (Window value : this.windows.values()) {
            ValidatedWindow validatedWindow;
            if (!(value instanceof ValidatedWindow) || (validatedWindow = (ValidatedWindow)((Object)value)).validate(ref, componentAccessor)) continue;
            value.close(ref, componentAccessor);
        }
    }

    public static <W extends Window> void closeAndRemoveAll(@Nonnull Map<UUID, W> windows) {
        Iterator<W> iterator = windows.values().iterator();
        while (iterator.hasNext()) {
            Ref<EntityStore> ref;
            Window window = (Window)iterator.next();
            PlayerRef playerRef = window.getPlayerRef();
            if (playerRef != null && (ref = playerRef.getReference()) != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                window.close(ref, store);
            }
            iterator.remove();
        }
    }

    @Nonnull
    public String toString() {
        return "WindowManager{windowId=" + String.valueOf(this.windowId) + ", windows=" + String.valueOf(this.windows) + "}";
    }
}

