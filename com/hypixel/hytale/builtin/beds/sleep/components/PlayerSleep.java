/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.components;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import java.time.Instant;

public sealed interface PlayerSleep {

    public record NoddingOff(Instant realTimeStart) implements PlayerSleep
    {
        public static PlayerSomnolence createComponent() {
            Instant now = Instant.now();
            NoddingOff state = new NoddingOff(now);
            return new PlayerSomnolence(state);
        }
    }

    public record Slumber(Instant gameTimeStart) implements PlayerSleep
    {
        public static PlayerSomnolence createComponent(WorldTimeResource worldTimeResource) {
            Instant now = worldTimeResource.getGameTime();
            Slumber state = new Slumber(now);
            return new PlayerSomnolence(state);
        }
    }

    public record MorningWakeUp(Instant gameTimeStart) implements PlayerSleep
    {
        public static PlayerSomnolence createComponent(WorldTimeResource worldTimeResource) {
            Instant now = worldTimeResource.getGameTime();
            MorningWakeUp state = new MorningWakeUp(now);
            return new PlayerSomnolence(state);
        }
    }

    public static enum FullyAwake implements PlayerSleep
    {
        INSTANCE;

    }
}

