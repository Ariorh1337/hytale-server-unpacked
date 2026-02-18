/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.sleep.SleepSoundsConfig;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SleepNotificationSystem
extends DelayedSystem<EntityStore> {
    public static final int SMALL_SERVER_PLAYER_COUNT = 4;
    public static final double BIG_SERVER_SLEEPERS_RATIO = 0.5;
    public static final String COLOR = "#5AB5B5";

    public SleepNotificationSystem() {
        super(1.0f);
    }

    @Override
    public void delayedTick(float dt, int systemIndex, @NonNullDecl Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        SleepSoundsConfig sleepSounds = world.getGameplayConfig().getWorldConfig().getSleepConfig().getSounds();
        if (sleepSounds.isNotificationLoopEnabled()) {
            SleepNotificationSystem.maybeDoNotification(store, true);
        }
    }

    public static void maybeDoNotification(Store<EntityStore> store, boolean fromAutoLoop) {
        World world = store.getExternalData().getWorld();
        SleepSoundsConfig sleepSounds = world.getGameplayConfig().getWorldConfig().getSleepConfig().getSounds();
        NotificationState state = SleepNotificationSystem.getNotificationState(world);
        if (state == NotReady.INSTANCE) {
            return;
        }
        long now = System.currentTimeMillis();
        WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
        if (!worldSomnolence.useSleepNotificationCooldown(now, sleepSounds.getNotificationLoopCooldownMs())) {
            return;
        }
        int soundIndex = fromAutoLoop ? sleepSounds.getNotificationLoopIndex() : sleepSounds.getNotificationIndex();
        Collection<PlayerRef> playerRefs = world.getPlayerRefs();
        for (PlayerRef playerRef : playerRefs) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || StartSlumberSystem.canNotifyOthersAboutTryingToSleep(store, ref)) continue;
            SoundUtil.playSoundEvent2dToPlayer(playerRef, soundIndex, SoundCategory.UI);
            if (state instanceof SmallServer) {
                if (state.readyToSleep() == 1) {
                    Message sleeperName = playerRefs.stream().map(PlayerRef::getReference).filter(r -> StartSlumberSystem.canNotifyOthersAboutTryingToSleep(store, r)).map(SleepNotificationSystem::getSleeperName).findFirst().orElseGet(() -> Message.raw("???"));
                    String msgKey = fromAutoLoop ? "server.interactions.sleep.notificationSingle" : "server.interactions.sleep.notificationSingleEnter";
                    playerRef.sendMessage(Message.translation(msgKey).color(COLOR).param("player", sleeperName));
                    continue;
                }
                playerRef.sendMessage(Message.translation("server.interactions.sleep.notificationSmall").color(COLOR).param("sleepers", state.readyToSleep()));
                continue;
            }
            if (!(state instanceof BigServer)) continue;
            playerRef.sendMessage(Message.translation("server.interactions.sleep.notificationBig").color(COLOR).param("sleepers", state.readyToSleep()).param("total", state.playerCount()));
        }
    }

    private static NotificationState getNotificationState(World world) {
        if (CanSleepInWorld.check(world).isNegative()) {
            return NotReady.INSTANCE;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        WorldSleep worldSleep = store.getResource(WorldSomnolence.getResourceType()).getState();
        if (worldSleep instanceof WorldSlumber) {
            return NotReady.INSTANCE;
        }
        List<Ref<EntityStore>> refs = world.getPlayerRefs().stream().map(PlayerRef::getReference).filter(Objects::nonNull).toList();
        int playerCount = refs.size();
        int readyToSleep = SleepNotificationSystem.countReadyToSleep(refs, store);
        if (playerCount <= 4) {
            boolean ready = readyToSleep > 0;
            return ready ? new SmallServer(readyToSleep, playerCount) : NotReady.INSTANCE;
        }
        double sleepersRatio = (double)readyToSleep / (double)playerCount;
        boolean ready = sleepersRatio >= 0.5;
        return ready ? new BigServer(sleepersRatio, readyToSleep, playerCount) : NotReady.INSTANCE;
    }

    private static int countReadyToSleep(Collection<Ref<EntityStore>> playerRefs, ComponentAccessor<EntityStore> store) {
        int count = 0;
        for (Ref<EntityStore> ref : playerRefs) {
            boolean readyToSleep;
            PlayerSomnolence somnolence = store.getComponent(ref, PlayerSomnolence.getComponentType());
            if (somnolence == null || !(readyToSleep = StartSlumberSystem.canNotifyOthersAboutTryingToSleep(store, ref))) continue;
            ++count;
        }
        return count;
    }

    public static Message getSleeperName(@Nullable Ref<EntityStore> ref) {
        Message lastSleeperDisplay;
        if (ref == null || !ref.isValid()) {
            return Message.raw("???");
        }
        Store<EntityStore> store = ref.getStore();
        DisplayNameComponent displayNameComponent = store.getComponent(ref, DisplayNameComponent.getComponentType());
        Message message = lastSleeperDisplay = displayNameComponent == null ? null : displayNameComponent.getDisplayName();
        if (lastSleeperDisplay != null) {
            return lastSleeperDisplay;
        }
        PlayerRef sleeperPlayerRef = store.getComponent(ref, PlayerRef.getComponentType());
        return Message.raw(sleeperPlayerRef == null ? "???" : sleeperPlayerRef.getUsername());
    }

    private static sealed interface NotificationState
    permits NotReady, SmallServer, BigServer {
        public int readyToSleep();

        public int playerCount();
    }

    private static enum NotReady implements NotificationState
    {
        INSTANCE;


        @Override
        public int readyToSleep() {
            return 0;
        }

        @Override
        public int playerCount() {
            return 0;
        }
    }

    private record SmallServer(int readyToSleep, int playerCount) implements NotificationState
    {
    }

    private record BigServer(double ratio, int readyToSleep, int playerCount) implements NotificationState
    {
    }
}

