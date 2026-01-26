/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.drbg;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource;
import org.bouncycastle.util.Properties;

class EntropyGatherer
implements Runnable {
    private static final Logger LOG = Logger.getLogger(EntropyGatherer.class.getName());
    private final long pause;
    private final AtomicBoolean seedAvailable;
    private final AtomicReference<byte[]> entropy;
    private final IncrementalEntropySource baseRandom;

    EntropyGatherer(IncrementalEntropySource incrementalEntropySource, AtomicBoolean atomicBoolean, AtomicReference<byte[]> atomicReference) {
        this.baseRandom = incrementalEntropySource;
        this.seedAvailable = atomicBoolean;
        this.entropy = atomicReference;
        this.pause = EntropyGatherer.getPause();
    }

    @Override
    public void run() {
        try {
            this.entropy.set(this.baseRandom.getEntropy(this.pause));
            this.seedAvailable.set(true);
        }
        catch (InterruptedException interruptedException) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("entropy request interrupted - exiting");
            }
            Thread.currentThread().interrupt();
        }
    }

    private static long getPause() {
        String string = Properties.getPropertyValue("org.bouncycastle.drbg.gather_pause_secs");
        if (string != null) {
            try {
                return Long.parseLong(string) * 1000L;
            }
            catch (Exception exception) {
                return 5000L;
            }
        }
        return 5000L;
    }
}

