/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class TimeInstrument {
    private int sampleCount;
    @Nonnull
    private Probe totalProbe;
    private String header;

    public TimeInstrument(@Nonnull String header) {
        this.header = header;
        this.sampleCount = 0;
        this.totalProbe = null;
    }

    public void takeSample(@Nonnull Probe probe) {
        if (this.totalProbe == null) {
            this.totalProbe = probe;
            ++this.sampleCount;
            return;
        }
        if (!probe.isCompatibleForAddition(this.totalProbe)) {
            LoggerUtil.getLogger().warning("Discarded a probe because of mismatched structures.");
            assert (false);
            return;
        }
        ++this.sampleCount;
        this.totalProbe.add(probe);
    }

    @Nonnull
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Performance Report\n");
        s.append("Sample Count: ").append(this.sampleCount).append("\n");
        s.append(this.header).append("\n");
        s.append(this.toString(0, this.totalProbe));
        return s.toString();
    }

    @Nonnull
    private String toString(int indentation, @Nonnull Probe probe) {
        long ms = probe.getTotalTime() / (long)this.sampleCount;
        ms /= 1000000L;
        StringBuilder s = new StringBuilder();
        s.append("\t".repeat(indentation));
        s.append(probe.getName()).append(": ");
        s.append(Long.toString(ms)).append(" ms");
        s.append("\n");
        List<Probe> childProbes = probe.getProbes();
        for (int i = 0; i < childProbes.size(); ++i) {
            s.append(this.toString(indentation + 1, childProbes.get(i)));
        }
        return s.toString();
    }

    public static class Probe {
        private final String name;
        private long startTime;
        private long totalTime;
        private State state;
        private List<Probe> probes;

        public Probe(@Nonnull String name) {
            this.name = name;
            this.state = State.NOT_STARTED;
            this.probes = new ArrayList<Probe>(1);
        }

        @Nonnull
        public Probe start() {
            this.startTime = System.nanoTime();
            this.state = State.STARTED;
            return this;
        }

        @Nonnull
        public Probe stop() {
            assert (this.state == State.STARTED);
            this.state = State.COMPLETED;
            this.totalTime = System.nanoTime() - this.startTime;
            return this;
        }

        public long getTotalTime() {
            assert (this.state == State.COMPLETED);
            return this.totalTime;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        @Nonnull
        public List<Probe> getProbes() {
            return this.probes;
        }

        @Nonnull
        public Probe createProbe(@Nonnull String name) {
            Probe probe = new Probe(name);
            this.probes.add(probe);
            return probe;
        }

        public boolean isCompatibleForAddition(@Nonnull Probe other) {
            if (this.probes.size() != other.probes.size() || !this.name.equals(other.name)) {
                return false;
            }
            for (int i = 0; i < this.probes.size(); ++i) {
                if (this.probes.get(i).isCompatibleForAddition(other.probes.get(i))) continue;
                return false;
            }
            return true;
        }

        public void add(@Nonnull Probe probe) {
            assert (this.state == State.COMPLETED);
            assert (this.isCompatibleForAddition(probe));
            this.totalTime += probe.getTotalTime();
            for (int i = 0; i < this.probes.size(); ++i) {
                this.probes.get(i).add(probe.probes.get(i));
            }
        }

        private static enum State {
            NOT_STARTED,
            STARTED,
            COMPLETED;

        }
    }
}

