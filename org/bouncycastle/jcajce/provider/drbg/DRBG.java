/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.drbg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.drbg.EntropyDaemon;
import org.bouncycastle.jcajce.provider.drbg.EntropyGatherer;
import org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource;
import org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySourceProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class DRBG {
    private static final String PREFIX = DRBG.class.getName();
    private static final String[][] initialEntropySourceNames = new String[][]{{"sun.security.provider.Sun", "sun.security.provider.SecureRandom"}, {"org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl"}, {"com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom"}, {"org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom"}};
    private static EntropyDaemon entropyDaemon = null;
    private static Thread entropyThread = null;

    private static int get256BitsEffectiveEntropySize() {
        int n = Properties.asInteger("org.bouncycastle.drbg.effective_256bits_entropy", 282);
        return (n + 7) / 8 * 8;
    }

    private static final Object[] findSource() {
        for (int i = 0; i < initialEntropySourceNames.length; ++i) {
            String[] stringArray = initialEntropySourceNames[i];
            try {
                Object[] objectArray = new Object[]{Class.forName(stringArray[0]).newInstance(), Class.forName(stringArray[1]).newInstance()};
                return objectArray;
            }
            catch (Throwable throwable) {
                continue;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static SecureRandom createBaseRandom(boolean bl) {
        if (Properties.getPropertyValue("org.bouncycastle.drbg.entropysource") != null) {
            EntropySourceProvider entropySourceProvider = DRBG.createEntropySource();
            EntropySource entropySource = entropySourceProvider.get(128);
            byte[] byArray = bl ? DRBG.generateDefaultPersonalizationString(entropySource.getEntropy()) : DRBG.generateNonceIVPersonalizationString(entropySource.getEntropy());
            return new SP800SecureRandomBuilder(entropySourceProvider).setPersonalizationString(byArray).buildHash(new SHA512Digest(), entropySource.getEntropy(), bl);
        }
        if (Properties.isOverrideSet("org.bouncycastle.drbg.entropy_thread")) {
            Object object = entropyDaemon;
            synchronized (object) {
                if (entropyThread == null) {
                    entropyThread = new Thread((Runnable)entropyDaemon, "BC Entropy Daemon");
                    entropyThread.setDaemon(true);
                    entropyThread.start();
                }
            }
            object = new HybridEntropySource(entropyDaemon, 256);
            byte[] byArray = bl ? DRBG.generateDefaultPersonalizationString(object.getEntropy()) : DRBG.generateNonceIVPersonalizationString(object.getEntropy());
            return new SP800SecureRandomBuilder(new EntropySourceProvider(){

                @Override
                public EntropySource get(int n) {
                    return new HybridEntropySource(entropyDaemon, n);
                }
            }).setPersonalizationString(byArray).buildHash(new SHA512Digest(), object.getEntropy(), bl);
        }
        OneShotHybridEntropySource oneShotHybridEntropySource = new OneShotHybridEntropySource(256);
        byte[] byArray = bl ? DRBG.generateDefaultPersonalizationString(oneShotHybridEntropySource.getEntropy()) : DRBG.generateNonceIVPersonalizationString(oneShotHybridEntropySource.getEntropy());
        return new SP800SecureRandomBuilder(new EntropySourceProvider(){

            @Override
            public EntropySource get(int n) {
                return new OneShotHybridEntropySource(n);
            }
        }).setPersonalizationString(byArray).buildHash(new SHA512Digest(), oneShotHybridEntropySource.getEntropy(), bl);
    }

    private static EntropySourceProvider createCoreEntropySourceProvider() {
        boolean bl = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                try {
                    Class<SecureRandom> clazz = SecureRandom.class;
                    return clazz.getMethod("getInstanceStrong", new Class[0]) != null;
                }
                catch (Exception exception) {
                    return false;
                }
            }
        });
        if (bl) {
            SecureRandom secureRandom = AccessController.doPrivileged(new PrivilegedAction<SecureRandom>(){

                @Override
                public SecureRandom run() {
                    try {
                        return (SecureRandom)SecureRandom.class.getMethod("getInstanceStrong", new Class[0]).invoke(null, new Object[0]);
                    }
                    catch (Exception exception) {
                        return null;
                    }
                }
            });
            if (secureRandom == null) {
                return DRBG.createInitialEntropySource();
            }
            return new IncrementalEntropySourceProvider(secureRandom, true);
        }
        return DRBG.createInitialEntropySource();
    }

    private static EntropySourceProvider createInitialEntropySource() {
        String string = AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return Security.getProperty("securerandom.source");
            }
        });
        if (string == null) {
            return new IncrementalEntropySourceProvider(new CoreSecureRandom(DRBG.findSource()), true);
        }
        try {
            return new URLSeededEntropySourceProvider(new URL(string));
        }
        catch (Exception exception) {
            return new IncrementalEntropySourceProvider(new CoreSecureRandom(DRBG.findSource()), true);
        }
    }

    private static EntropySourceProvider createEntropySource() {
        final String string = Properties.getPropertyValue("org.bouncycastle.drbg.entropysource");
        return AccessController.doPrivileged(new PrivilegedAction<EntropySourceProvider>(){

            @Override
            public EntropySourceProvider run() {
                try {
                    Class clazz = ClassUtil.loadClass(DRBG.class, string);
                    return (EntropySourceProvider)clazz.newInstance();
                }
                catch (Exception exception) {
                    throw new IllegalStateException("entropy source " + string + " not created: " + exception.getMessage(), exception);
                }
            }
        });
    }

    private static byte[] generateDefaultPersonalizationString(byte[] byArray) {
        return Arrays.concatenate(Strings.toByteArray("Default"), byArray, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }

    private static byte[] generateNonceIVPersonalizationString(byte[] byArray) {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), byArray, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }

    private static void sleep(long l) throws InterruptedException {
        if (l != 0L) {
            Thread.sleep(l);
        }
    }

    static /* synthetic */ SecureRandom access$100(boolean bl) {
        return DRBG.createBaseRandom(bl);
    }

    static {
        entropyDaemon = new EntropyDaemon();
    }

    private static class CoreSecureRandom
    extends SecureRandom {
        CoreSecureRandom(Object[] objectArray) {
            super((SecureRandomSpi)objectArray[1], (Provider)objectArray[0]);
        }
    }

    public static class Default
    extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.access$100(true);

        @Override
        protected void engineSetSeed(byte[] byArray) {
            random.setSeed(byArray);
        }

        @Override
        protected void engineNextBytes(byte[] byArray) {
            random.nextBytes(byArray);
        }

        @Override
        protected byte[] engineGenerateSeed(int n) {
            return random.generateSeed(n);
        }
    }

    private static class HybridEntropySource
    implements EntropySource {
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final SP800SecureRandom drbg;
        private final SignallingEntropySource entropySource;
        private final int bytesRequired;
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        HybridEntropySource(EntropyDaemon entropyDaemon, int n) {
            EntropySourceProvider entropySourceProvider = DRBG.createCoreEntropySourceProvider();
            this.bytesRequired = (n + 7) / 8;
            this.entropySource = new SignallingEntropySource(entropyDaemon, this.seedAvailable, entropySourceProvider, DRBG.get256BitsEffectiveEntropySize());
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider(){

                @Override
                public EntropySource get(int n) {
                    return entropySource;
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.entropySource.getEntropy(), false);
        }

        @Override
        public boolean isPredictionResistant() {
            return true;
        }

        @Override
        public byte[] getEntropy() {
            byte[] byArray = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 128) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            this.drbg.nextBytes(byArray);
            return byArray;
        }

        @Override
        public int entropySize() {
            return this.bytesRequired * 8;
        }

        private static class SignallingEntropySource
        implements IncrementalEntropySource {
            private final EntropyDaemon entropyDaemon;
            private final AtomicBoolean seedAvailable;
            private final IncrementalEntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            SignallingEntropySource(EntropyDaemon entropyDaemon, AtomicBoolean atomicBoolean, EntropySourceProvider entropySourceProvider, int n) {
                this.entropyDaemon = entropyDaemon;
                this.seedAvailable = atomicBoolean;
                this.entropySource = (IncrementalEntropySource)entropySourceProvider.get(n);
                this.byteLength = (n + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return true;
            }

            @Override
            public byte[] getEntropy() {
                try {
                    return this.getEntropy(0L);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override
            public byte[] getEntropy(long l) throws InterruptedException {
                byte[] byArray = this.entropy.getAndSet(null);
                if (byArray == null || byArray.length != this.byteLength) {
                    byArray = this.entropySource.getEntropy(l);
                } else {
                    this.scheduled.set(false);
                }
                return byArray;
            }

            void schedule() {
                if (!this.scheduled.getAndSet(true)) {
                    this.entropyDaemon.addTask(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
                }
            }

            @Override
            public int entropySize() {
                return this.byteLength * 8;
            }
        }
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecureRandom.DEFAULT", PREFIX + "$Default");
            configurableProvider.addAlgorithm("SecureRandom.NONCEANDIV", PREFIX + "$NonceAndIV");
        }
    }

    public static class NonceAndIV
    extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.access$100(false);

        @Override
        protected void engineSetSeed(byte[] byArray) {
            random.setSeed(byArray);
        }

        @Override
        protected void engineNextBytes(byte[] byArray) {
            random.nextBytes(byArray);
        }

        @Override
        protected byte[] engineGenerateSeed(int n) {
            return random.generateSeed(n);
        }
    }

    private static class OneShotHybridEntropySource
    implements EntropySource {
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final SP800SecureRandom drbg;
        private final OneShotSignallingEntropySource entropySource;
        private final int bytesRequired;
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        OneShotHybridEntropySource(int n) {
            EntropySourceProvider entropySourceProvider = DRBG.createCoreEntropySourceProvider();
            this.bytesRequired = (n + 7) / 8;
            this.entropySource = new OneShotSignallingEntropySource(this.seedAvailable, entropySourceProvider, DRBG.get256BitsEffectiveEntropySize());
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider(){

                @Override
                public EntropySource get(int n) {
                    return entropySource;
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.entropySource.getEntropy(), false);
        }

        @Override
        public boolean isPredictionResistant() {
            return true;
        }

        @Override
        public byte[] getEntropy() {
            byte[] byArray = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 1024) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            this.drbg.nextBytes(byArray);
            return byArray;
        }

        @Override
        public int entropySize() {
            return this.bytesRequired * 8;
        }

        private static class OneShotSignallingEntropySource
        implements IncrementalEntropySource {
            private final AtomicBoolean seedAvailable;
            private final IncrementalEntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            OneShotSignallingEntropySource(AtomicBoolean atomicBoolean, EntropySourceProvider entropySourceProvider, int n) {
                this.seedAvailable = atomicBoolean;
                this.entropySource = (IncrementalEntropySource)entropySourceProvider.get(n);
                this.byteLength = (n + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return true;
            }

            @Override
            public byte[] getEntropy() {
                try {
                    return this.getEntropy(0L);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override
            public byte[] getEntropy(long l) throws InterruptedException {
                byte[] byArray = this.entropy.getAndSet(null);
                if (byArray == null || byArray.length != this.byteLength) {
                    byArray = this.entropySource.getEntropy(l);
                } else {
                    this.scheduled.set(false);
                }
                return byArray;
            }

            void schedule() {
                if (!this.scheduled.getAndSet(true)) {
                    Thread thread = new Thread(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
                    thread.setDaemon(true);
                    thread.start();
                }
            }

            @Override
            public int entropySize() {
                return this.byteLength * 8;
            }
        }
    }

    private static class URLSeededEntropySourceProvider
    implements EntropySourceProvider {
        private final InputStream seedStream;

        URLSeededEntropySourceProvider(final URL uRL) {
            this.seedStream = AccessController.doPrivileged(new PrivilegedAction<InputStream>(){
                final /* synthetic */ URLSeededEntropySourceProvider this$0;
                {
                    this.this$0 = uRLSeededEntropySourceProvider;
                }

                @Override
                public InputStream run() {
                    try {
                        return uRL.openStream();
                    }
                    catch (IOException iOException) {
                        throw new IllegalStateException("unable to open random source");
                    }
                }
            });
        }

        private int privilegedRead(final byte[] byArray, final int n, final int n2) {
            return AccessController.doPrivileged(new PrivilegedAction<Integer>(){
                final /* synthetic */ URLSeededEntropySourceProvider this$0;
                {
                    this.this$0 = uRLSeededEntropySourceProvider;
                }

                @Override
                public Integer run() {
                    try {
                        return this.this$0.seedStream.read(byArray, n, n2);
                    }
                    catch (IOException iOException) {
                        throw new InternalError("unable to read random source");
                    }
                }
            });
        }

        @Override
        public EntropySource get(final int n) {
            return new IncrementalEntropySource(){
                private final int numBytes;
                final /* synthetic */ URLSeededEntropySourceProvider this$0;
                {
                    this.this$0 = uRLSeededEntropySourceProvider;
                    this.numBytes = (n + 7) / 8;
                }

                @Override
                public boolean isPredictionResistant() {
                    return true;
                }

                @Override
                public byte[] getEntropy() {
                    try {
                        return this.getEntropy(0L);
                    }
                    catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("initial entropy fetch interrupted");
                    }
                }

                @Override
                public byte[] getEntropy(long l) throws InterruptedException {
                    int n3;
                    int n2;
                    byte[] byArray = new byte[this.numBytes];
                    for (n3 = 0; n3 != byArray.length && (n2 = this.this$0.privilegedRead(byArray, n3, byArray.length - n3)) > -1; n3 += n2) {
                        DRBG.sleep(l);
                    }
                    if (n3 != byArray.length) {
                        throw new InternalError("unable to fully read random source");
                    }
                    return byArray;
                }

                @Override
                public int entropySize() {
                    return n;
                }
            };
        }
    }
}

