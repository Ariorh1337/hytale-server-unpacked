/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.world;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nonnull;

public class UpdateWorldSlumberSystem
extends TickingSystem<EntityStore> {
    @Override
    public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
        boolean sleepingIsOver;
        World world = store.getExternalData().getWorld();
        WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
        WorldSleep worldSleep = worldSomnolence.getState();
        if (!(worldSleep instanceof WorldSlumber)) {
            return;
        }
        WorldSlumber slumber = (WorldSlumber)worldSleep;
        slumber.incProgressSeconds(dt);
        boolean bl = sleepingIsOver = slumber.getProgressSeconds() >= slumber.getIrlDurationSeconds() || UpdateWorldSlumberSystem.isSomeoneAwake(store);
        if (!sleepingIsOver) {
            return;
        }
        worldSomnolence.setState(WorldSleep.Awake.INSTANCE);
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        Instant wakeUpTime = UpdateWorldSlumberSystem.computeWakeupTime(slumber);
        timeResource.setGameTime(wakeUpTime, world, store);
        store.forEachEntityParallel(PlayerSomnolence.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
            PlayerSomnolence somnolenceComponent = archetypeChunk.getComponent(index, PlayerSomnolence.getComponentType());
            assert (somnolenceComponent != null);
            if (somnolenceComponent.getSleepState() instanceof PlayerSleep.Slumber) {
                Ref ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.MorningWakeUp.createComponent(timeResource));
            }
        });
    }

    private static Instant computeWakeupTime(@Nonnull WorldSlumber slumber) {
        float progress = slumber.getProgressSeconds() / slumber.getIrlDurationSeconds();
        long totalNanos = Duration.between(slumber.getStartInstant(), slumber.getTargetInstant()).toNanos();
        long progressNanos = (long)((float)totalNanos * progress);
        return slumber.getStartInstant().plusNanos(progressNanos);
    }

    private static boolean isSomeoneAwake(@Nonnull ComponentAccessor<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        Collection<PlayerRef> playerRefs = world.getPlayerRefs();
        if (playerRefs.isEmpty()) {
            return false;
        }
        Iterator<PlayerRef> iterator = playerRefs.iterator();
        if (iterator.hasNext()) {
            PlayerRef playerRef = iterator.next();
            PlayerSomnolence somnolenceComponent = store.getComponent(playerRef.getReference(), PlayerSomnolence.getComponentType());
            if (somnolenceComponent == null) {
                return true;
            }
            PlayerSleep sleepState = somnolenceComponent.getSleepState();
            return sleepState instanceof PlayerSleep.FullyAwake;
        }
        return false;
    }
}

