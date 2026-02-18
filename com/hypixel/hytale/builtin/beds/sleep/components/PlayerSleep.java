/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.components;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public sealed interface PlayerSleep {

    public record NoddingOff(Instant realTimeStart) implements PlayerSleep
    {
        @Nonnull
        public static PlayerSomnolence createComponent() {
            Instant now = Instant.now();
            NoddingOff state = new NoddingOff(now);
            return new PlayerSomnolence(state);
        }
    }

    public record Slumber(Instant gameTimeStart) implements PlayerSleep
    {
        @Nonnull
        public static PlayerSomnolence createComponent(@Nonnull WorldTimeResource worldTimeResource) {
            Instant now = worldTimeResource.getGameTime();
            Slumber state = new Slumber(now);
            return new PlayerSomnolence(state);
        }
    }

    public record MorningWakeUp(@Nullable Instant gameTimeStart) implements PlayerSleep
    {
        private static final Duration WAKE_UP_AUTOSLEEP_DELAY = Duration.ofHours(1L);

        @Nonnull
        public static PlayerSomnolence createComponent(@Nullable Instant gameTimeStart) {
            MorningWakeUp state = new MorningWakeUp(gameTimeStart);
            return new PlayerSomnolence(state);
        }

        public boolean isReadyToSleepAgain(Instant worldTime) {
            if (this.gameTimeStart == null) {
                return true;
            }
            Instant readyTime = worldTime.plus(WAKE_UP_AUTOSLEEP_DELAY);
            return worldTime.isAfter(readyTime);
        }
    }

    public static enum FullyAwake implements PlayerSleep
    {
        INSTANCE;

    }
}

