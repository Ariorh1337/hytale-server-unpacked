/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.annotation.Nullable;

public class SleepConfig {
    public static final BuilderCodec<SleepConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SleepConfig.class, SleepConfig::new).append(new KeyedCodec<Float>("WakeUpHour", Codec.FLOAT), (sleepConfig, i) -> {
        sleepConfig.wakeUpHour = i.floatValue();
    }, o -> Float.valueOf(o.wakeUpHour)).documentation("The in-game hour at which players naturally wake up from sleep.").add()).append(new KeyedCodec<double[]>("AllowedSleepHoursRange", Codec.DOUBLE_ARRAY), (sleepConfig, i) -> {
        sleepConfig.allowedSleepHoursRange = i;
    }, o -> o.allowedSleepHoursRange).addValidator(Validators.doubleArraySize(2)).documentation("The in-game hours during which players can sleep to skip to the WakeUpHour. If missing, there is no restriction.").add()).build();
    public static final SleepConfig DEFAULT = new SleepConfig();
    private float wakeUpHour = 5.5f;
    private double[] allowedSleepHoursRange;

    public float getWakeUpHour() {
        return this.wakeUpHour;
    }

    @Nullable
    public double[] getAllowedSleepHoursRange() {
        return this.allowedSleepHoursRange;
    }

    @Nullable
    public LocalTime getSleepStartTime() {
        if (this.allowedSleepHoursRange == null) {
            return null;
        }
        double sleepStartHour = this.allowedSleepHoursRange[0];
        int hour = (int)sleepStartHour;
        int minute = (int)((sleepStartHour - (double)hour) * 60.0);
        return LocalTime.of(hour, minute);
    }

    public boolean isWithinSleepHoursRange(LocalDateTime gameTime) {
        double max;
        double min;
        if (this.allowedSleepHoursRange == null) {
            return true;
        }
        float hour = SleepConfig.getFractionalHourOfDay(gameTime);
        return ((double)hour - (min = this.allowedSleepHoursRange[0]) + 24.0) % 24.0 <= ((max = this.allowedSleepHoursRange[1]) - min + 24.0) % 24.0;
    }

    public Duration computeDurationUntilSleep(LocalDateTime now) {
        if (this.allowedSleepHoursRange == null) {
            return Duration.ZERO;
        }
        float currentHour = SleepConfig.getFractionalHourOfDay(now);
        double sleepStartHour = this.allowedSleepHoursRange[0];
        double hoursUntilSleep = (sleepStartHour - (double)currentHour + 24.0) % 24.0;
        long seconds = (long)(hoursUntilSleep * 3600.0);
        return Duration.ofSeconds(seconds);
    }

    private static float getFractionalHourOfDay(LocalDateTime dateTime) {
        return (float)dateTime.getHour() + (float)dateTime.getMinute() / 60.0f + (float)dateTime.getSecond() / 3600.0f;
    }
}

