/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

public class AudioUtil {
    public static final float MIN_DECIBEL_VOLUME = -100.0f;
    public static final float MAX_DECIBEL_VOLUME = 10.0f;
    public static final float MIN_SEMITONE_PITCH = -12.0f;
    public static final float MAX_SEMITONE_PITCH = 12.0f;

    public static float decibelsToLinearGain(float decibels) {
        if (decibels <= -100.0f) {
            return 0.0f;
        }
        return (float)Math.pow(10.0, decibels / 20.0f);
    }

    public static float linearGainToDecibels(float linearGain) {
        if (linearGain <= 0.0f) {
            return -100.0f;
        }
        return (float)(Math.log(linearGain) / Math.log(10.0) * 20.0);
    }

    public static float semitonesToLinearPitch(float semitones) {
        return (float)(1.0 / Math.pow(2.0, -semitones / 12.0f));
    }

    public static float linearPitchToSemitones(float linearPitch) {
        return (float)(Math.log(linearPitch) / Math.log(2.0) * 12.0);
    }
}

