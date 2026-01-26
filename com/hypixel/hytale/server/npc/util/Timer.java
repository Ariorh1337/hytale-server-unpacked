/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.math.random.RandomExtra;
import java.util.function.Supplier;

public class Timer
implements Tickable {
    private double value = 0.0;
    private double minRestartValue;
    private double maxValue;
    private double rate;
    private boolean repeating;
    private TimerState state = TimerState.STOPPED;
    private boolean initialised;

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setMinRestartValue(double minRestartValue) {
        this.minRestartValue = minRestartValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setValue(double v) {
        this.value = Math.min(v, this.maxValue);
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public double getValue() {
        return this.value;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    @Override
    public void tick(float dt) {
        if (this.state != TimerState.RUNNING) {
            return;
        }
        this.value -= (double)dt * this.rate;
        if (this.value > 0.0) {
            return;
        }
        if (this.repeating) {
            double offset = this.value;
            this.restart();
            this.value -= offset;
            return;
        }
        this.state = TimerState.STOPPED;
        this.value = 0.0;
    }

    public void start(double minStartValue, double maxStartValue, double minRestartValue, double maxValue, double rate, boolean repeating) {
        this.value = RandomExtra.randomRange(minStartValue, maxStartValue);
        this.minRestartValue = minRestartValue;
        this.maxValue = maxValue;
        this.rate = rate;
        this.repeating = repeating;
        this.state = TimerState.RUNNING;
        this.initialised = true;
    }

    public void resume() {
        if (this.isPaused()) {
            this.state = TimerState.RUNNING;
        }
    }

    public void pause() {
        if (this.isRunning()) {
            this.state = TimerState.PAUSED;
        }
    }

    public void addValue(double v) {
        this.value = Math.min(this.value + v, this.maxValue);
    }

    public void stop() {
        this.state = TimerState.STOPPED;
    }

    public void restart() {
        this.value = this.pickNextTimerValue();
        this.state = TimerState.RUNNING;
    }

    public boolean isInState(TimerState s) {
        return s == TimerState.ANY || this.state == s;
    }

    public boolean isPaused() {
        return this.state == TimerState.PAUSED;
    }

    public boolean isRunning() {
        return this.state == TimerState.RUNNING;
    }

    public boolean isStopped() {
        return this.state == TimerState.STOPPED;
    }

    private double pickNextTimerValue() {
        return RandomExtra.randomRange(this.minRestartValue, this.maxValue);
    }

    public static enum TimerState implements Supplier<String>
    {
        RUNNING("Running"),
        PAUSED("Paused"),
        STOPPED("Stopped"),
        ANY("Any");

        private final String asText;

        private TimerState(String text) {
            this.asText = text;
        }

        public String asText() {
            return this.asText;
        }

        @Override
        public String get() {
            return this.asText;
        }
    }

    public static enum TimerAction implements Supplier<String>
    {
        START("Start"),
        STOP("Stop"),
        PAUSE("Pause"),
        CONTINUE("Continue"),
        RESTART("Restart"),
        MODIFY("Modify");

        private final String asText;

        private TimerAction(String text) {
            this.asText = text;
        }

        public String asText() {
            return this.asText;
        }

        @Override
        public String get() {
            return this.asText;
        }
    }
}

