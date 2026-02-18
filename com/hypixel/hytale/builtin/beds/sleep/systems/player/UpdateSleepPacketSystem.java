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
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.packets.world.SleepClock;
import com.hypixel.hytale.protocol.packets.world.SleepMultiplayer;
import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.runtime.SwitchBootstraps;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateSleepPacketSystem
extends DelayedEntitySystem<EntityStore> {
    private static final int MAX_SAMPLE_COUNT = 5;
    private static final float SYSTEM_INTERVAL_S = 0.25f;
    @Nonnull
    private static final Duration SPAN_BEFORE_BLACK_SCREEN = Duration.ofMillis(1200L);
    @Nonnull
    private static final UUID[] EMPTY_UUIDS = new UUID[0];
    @Nonnull
    private static final UpdateSleepState PACKET_NO_SLEEP_UI = new UpdateSleepState(false, false, null, null);
    @Nonnull
    private final ComponentType<EntityStore, PlayerRef> playerRefComponentType;
    @Nonnull
    private final ComponentType<EntityStore, PlayerSomnolence> playerSomnolenceComponentType;
    @Nonnull
    private final ComponentType<EntityStore, SleepTracker> sleepTrackerComponentType;
    @Nonnull
    private final ResourceType<EntityStore, WorldSomnolence> worldSomnolenceResourceType;
    @Nonnull
    private final ResourceType<EntityStore, WorldTimeResource> worldTimeResourceType;
    @Nonnull
    private final Query<EntityStore> query;

    public UpdateSleepPacketSystem(@Nonnull ComponentType<EntityStore, PlayerRef> playerRefComponentType, @Nonnull ComponentType<EntityStore, PlayerSomnolence> playerSomnolenceComponentType, @Nonnull ComponentType<EntityStore, SleepTracker> sleepTrackerComponentType, @Nonnull ResourceType<EntityStore, WorldSomnolence> worldSomnolenceResourceType, @Nonnull ResourceType<EntityStore, WorldTimeResource> worldTimeResourceType) {
        super(0.25f);
        this.playerRefComponentType = playerRefComponentType;
        this.playerSomnolenceComponentType = playerSomnolenceComponentType;
        this.sleepTrackerComponentType = sleepTrackerComponentType;
        this.worldSomnolenceResourceType = worldSomnolenceResourceType;
        this.worldTimeResourceType = worldTimeResourceType;
        this.query = Query.and(playerRefComponentType, playerSomnolenceComponentType, sleepTrackerComponentType);
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        UpdateSleepState packet = this.createSleepPacket(store, index, archetypeChunk);
        SleepTracker sleepTrackerComponent = archetypeChunk.getComponent(index, this.sleepTrackerComponentType);
        assert (sleepTrackerComponent != null);
        if ((packet = sleepTrackerComponent.generatePacketToSend(packet)) != null) {
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, this.playerRefComponentType);
            assert (playerRefComponent != null);
            playerRefComponent.getPacketHandler().write((ToClientPacket)packet);
        }
    }

    @Nonnull
    private UpdateSleepState createSleepPacket(@Nonnull Store<EntityStore> store, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk) {
        SleepClock sleepClock;
        World world = store.getExternalData().getWorld();
        WorldSomnolence worldSomnolenceResource = store.getResource(this.worldSomnolenceResourceType);
        WorldSleep worldSleepState = worldSomnolenceResource.getState();
        PlayerSomnolence playerSomnolenceComponent = archetypeChunk.getComponent(index, this.playerSomnolenceComponentType);
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
        ObjectArrayList<PlayerRef> playerRefs = new ObjectArrayList<PlayerRef>(world.getPlayerRefs());
        playerRefs.removeIf(playerRef -> playerRef.getReference() == null);
        if (playerRefs.size() <= 1) {
            return null;
        }
        playerRefs.sort(Comparator.comparingLong(ref -> ref.getUuid().hashCode() + world.hashCode()));
        int sleepersCount = 0;
        int awakeCount = 0;
        ObjectArrayList awakeSampleList = new ObjectArrayList(playerRefs.size());
        for (PlayerRef playerRef2 : playerRefs) {
            Ref<EntityStore> ref2 = playerRef2.getReference();
            if (ref2 == null || !ref2.isValid()) continue;
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

