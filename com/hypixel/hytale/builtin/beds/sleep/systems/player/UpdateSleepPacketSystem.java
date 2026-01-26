/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.components.SleepTracker;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.world.SleepClock;
import com.hypixel.hytale.protocol.packets.world.SleepMultiplayer;
import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.runtime.SwitchBootstraps;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateSleepPacketSystem
extends DelayedEntitySystem<EntityStore> {
    public static final Query<EntityStore> QUERY = Query.and(PlayerRef.getComponentType(), PlayerSomnolence.getComponentType(), SleepTracker.getComponentType());
    public static final Duration SPAN_BEFORE_BLACK_SCREEN = Duration.ofMillis(1200L);
    public static final int MAX_SAMPLE_COUNT = 5;
    private static final UUID[] EMPTY_UUIDS = new UUID[0];
    private static final UpdateSleepState PACKET_NO_SLEEP_UI = new UpdateSleepState(false, false, null, null);

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    public UpdateSleepPacketSystem() {
        super(0.25f);
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        UpdateSleepState packet = this.createSleepPacket(store, index, archetypeChunk);
        SleepTracker sleepTrackerComponent = archetypeChunk.getComponent(index, SleepTracker.getComponentType());
        assert (sleepTrackerComponent != null);
        if ((packet = sleepTrackerComponent.generatePacketToSend(packet)) != null) {
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerRefComponent.getPacketHandler().write((Packet)packet);
        }
    }

    private UpdateSleepState createSleepPacket(@Nonnull Store<EntityStore> store, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk) {
        SleepClock sleepClock;
        World world = store.getExternalData().getWorld();
        WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
        WorldSleep worldSleepState = worldSomnolence.getState();
        PlayerSomnolence playerSomnolenceComponent = archetypeChunk.getComponent(index, PlayerSomnolence.getComponentType());
        assert (playerSomnolenceComponent != null);
        PlayerSleep playerSleepState = playerSomnolenceComponent.getSleepState();
        if (worldSleepState instanceof WorldSlumber) {
            WorldSlumber slumber = (WorldSlumber)worldSleepState;
            sleepClock = slumber.createSleepClock();
        } else {
            sleepClock = null;
        }
        SleepClock clock = sleepClock;
        PlayerSleep playerSleep = playerSleepState;
        Objects.requireNonNull(playerSleep);
        PlayerSleep playerSleep2 = playerSleep;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerSleep.FullyAwake.class, PlayerSleep.MorningWakeUp.class, PlayerSleep.NoddingOff.class, PlayerSleep.Slumber.class}, (Object)playerSleep2, n)) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                PlayerSleep.FullyAwake ignored = (PlayerSleep.FullyAwake)playerSleep2;
                yield PACKET_NO_SLEEP_UI;
            }
            case 1 -> {
                PlayerSleep.MorningWakeUp ignored = (PlayerSleep.MorningWakeUp)playerSleep2;
                yield PACKET_NO_SLEEP_UI;
            }
            case 2 -> {
                PlayerSleep.NoddingOff noddingOff = (PlayerSleep.NoddingOff)playerSleep2;
                if (CanSleepInWorld.check(world).isNegative()) {
                    yield PACKET_NO_SLEEP_UI;
                }
                long elapsedMs = Duration.between(noddingOff.realTimeStart(), Instant.now()).toMillis();
                boolean grayFade = elapsedMs > SPAN_BEFORE_BLACK_SCREEN.toMillis();
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                boolean readyToSleep = StartSlumberSystem.isReadyToSleep(store, ref);
                yield new UpdateSleepState(grayFade, false, clock, readyToSleep ? this.createSleepMultiplayer(store) : null);
            }
            case 3 -> {
                PlayerSleep.Slumber ignored = (PlayerSleep.Slumber)playerSleep2;
                yield new UpdateSleepState(true, true, clock, null);
            }
        };
    }

    @Nullable
    private SleepMultiplayer createSleepMultiplayer(@Nonnull Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        ArrayList<PlayerRef> playerRefs = new ArrayList<PlayerRef>(world.getPlayerRefs());
        playerRefs.removeIf(playerRef -> playerRef.getReference() == null);
        if (playerRefs.size() <= 1) {
            return null;
        }
        playerRefs.sort(Comparator.comparingLong(ref -> ref.getUuid().hashCode() + world.hashCode()));
        int sleepersCount = 0;
        int awakeCount = 0;
        ArrayList<UUID> awakeSampleList = new ArrayList<UUID>(playerRefs.size());
        for (PlayerRef playerRef2 : playerRefs) {
            Ref<EntityStore> ref2 = playerRef2.getReference();
            boolean readyToSleep = StartSlumberSystem.isReadyToSleep(store, ref2);
            if (readyToSleep) {
                ++sleepersCount;
                continue;
            }
            ++awakeCount;
            awakeSampleList.add(playerRef2.getUuid());
        }
        UUID[] awakeSample = awakeSampleList.size() > 5 ? EMPTY_UUIDS : (UUID[])awakeSampleList.toArray(UUID[]::new);
        return new SleepMultiplayer(sleepersCount, awakeCount, awakeSample);
    }
}

