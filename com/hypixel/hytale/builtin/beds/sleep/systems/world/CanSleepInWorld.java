/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.world;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.gameplay.SleepConfig;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.LocalDateTime;

public final class CanSleepInWorld {
    public static Result check(World world) {
        if (world.getWorldConfig().isGameTimePaused()) {
            return Status.GAME_TIME_PAUSED;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        LocalDateTime worldTime = store.getResource(WorldTimeResource.getResourceType()).getGameDateTime();
        SleepConfig sleepConfig = world.getGameplayConfig().getWorldConfig().getSleepConfig();
        if (!sleepConfig.isWithinSleepHoursRange(worldTime)) {
            return new NotDuringSleepHoursRange(worldTime, sleepConfig);
        }
        return Status.CAN_SLEEP;
    }

    public static enum Status implements Result
    {
        CAN_SLEEP,
        GAME_TIME_PAUSED;


        @Override
        public boolean isNegative() {
            return this != CAN_SLEEP;
        }
    }

    public record NotDuringSleepHoursRange(LocalDateTime worldTime, SleepConfig sleepConfig) implements Result
    {
        @Override
        public boolean isNegative() {
            return true;
        }
    }

    public static sealed interface Result
    permits NotDuringSleepHoursRange, Status {
        public boolean isNegative();
    }
}

