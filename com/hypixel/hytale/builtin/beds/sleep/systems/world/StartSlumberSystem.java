/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.world;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.runtime.SwitchBootstraps;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class StartSlumberSystem
extends DelayedSystem<EntityStore> {
    public static final Duration NODDING_OFF_DURATION = Duration.ofMillis(3200L);
    public static final Duration WAKE_UP_AUTOSLEEP_DELAY = Duration.ofHours(1L);

    public StartSlumberSystem() {
        super(0.3f);
    }

    @Override
    public void delayedTick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
        this.checkIfEveryoneIsReadyToSleep(store);
    }

    private void checkIfEveryoneIsReadyToSleep(Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        Collection<PlayerRef> playerRefs = world.getPlayerRefs();
        if (playerRefs.isEmpty()) {
            return;
        }
        if (CanSleepInWorld.check(world).isNegative()) {
            return;
        }
        float wakeUpHour = world.getGameplayConfig().getWorldConfig().getSleepConfig().getWakeUpHour();
        WorldSomnolence worldSomnolenceResource = store.getResource(WorldSomnolence.getResourceType());
        WorldSleep worldState = worldSomnolenceResource.getState();
        if (worldState != WorldSleep.Awake.INSTANCE) {
            return;
        }
        if (this.isEveryoneReadyToSleep(store)) {
            WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
            Instant now = timeResource.getGameTime();
            Instant target = this.computeWakeupInstant(now, wakeUpHour);
            float irlSeconds = StartSlumberSystem.computeIrlSeconds(now, target);
            worldSomnolenceResource.setState(new WorldSlumber(now, target, irlSeconds));
            store.forEachEntityParallel(PlayerSomnolence.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
                Ref ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.Slumber.createComponent(timeResource));
            });
        }
    }

    private Instant computeWakeupInstant(@Nonnull Instant now, float wakeUpHour) {
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        int hours = (int)wakeUpHour;
        float fractionalHour = wakeUpHour - (float)hours;
        LocalDateTime wakeUpTime = ldt.toLocalDate().atTime(hours, (int)(fractionalHour * 60.0f));
        if (!ldt.isBefore(wakeUpTime)) {
            wakeUpTime = wakeUpTime.plusDays(1L);
        }
        return wakeUpTime.toInstant(ZoneOffset.UTC);
    }

    private static float computeIrlSeconds(Instant startInstant, Instant targetInstant) {
        long ms = Duration.between(startInstant, targetInstant).toMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        double seconds = Math.max(3.0, (double)hours / 6.0);
        return (float)Math.ceil(seconds);
    }

    private boolean isEveryoneReadyToSleep(ComponentAccessor<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        Collection<PlayerRef> playerRefs = world.getPlayerRefs();
        if (playerRefs.isEmpty()) {
            return false;
        }
        for (PlayerRef playerRef : playerRefs) {
            if (StartSlumberSystem.isReadyToSleep(store, playerRef.getReference())) continue;
            return false;
        }
        return true;
    }

    public static boolean isReadyToSleep(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref) {
        PlayerSleep sleepState;
        PlayerSomnolence somnolence = store.getComponent(ref, PlayerSomnolence.getComponentType());
        if (somnolence == null) {
            return false;
        }
        PlayerSleep playerSleep = sleepState = somnolence.getSleepState();
        Objects.requireNonNull(playerSleep);
        PlayerSleep playerSleep2 = playerSleep;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerSleep.FullyAwake.class, PlayerSleep.MorningWakeUp.class, PlayerSleep.NoddingOff.class, PlayerSleep.Slumber.class}, (Object)playerSleep2, n)) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                PlayerSleep.FullyAwake fullyAwake = (PlayerSleep.FullyAwake)playerSleep2;
                yield false;
            }
            case 1 -> {
                PlayerSleep.MorningWakeUp morningWakeUp = (PlayerSleep.MorningWakeUp)playerSleep2;
                WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
                Instant readyTime = morningWakeUp.gameTimeStart().plus(WAKE_UP_AUTOSLEEP_DELAY);
                yield worldTimeResource.getGameTime().isAfter(readyTime);
            }
            case 2 -> {
                PlayerSleep.NoddingOff noddingOff = (PlayerSleep.NoddingOff)playerSleep2;
                Instant sleepStart = noddingOff.realTimeStart().plus(NODDING_OFF_DURATION);
                yield Instant.now().isAfter(sleepStart);
            }
            case 3 -> {
                PlayerSleep.Slumber slumber = (PlayerSleep.Slumber)playerSleep2;
                yield true;
            }
        };
    }
}

