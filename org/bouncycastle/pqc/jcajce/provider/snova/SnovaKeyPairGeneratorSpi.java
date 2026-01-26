/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.snova;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyPairGenerator;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.snova.BCSnovaPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.snova.BCSnovaPublicKey;
import org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;
import org.bouncycastle.util.Strings;

public class SnovaKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    SnovaKeyGenerationParameters param;
    private SnovaParameters snovaParameters;
    SnovaKeyPairGenerator engine = new SnovaKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public SnovaKeyPairGeneratorSpi() {
        super("Snova");
    }

    protected SnovaKeyPairGeneratorSpi(SnovaParameters snovaParameters) {
        super(snovaParameters.getName());
        this.snovaParameters = snovaParameters;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = SnovaKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new SnovaKeyGenerationParameters(secureRandom, (SnovaParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof SnovaParameterSpec) {
            SnovaParameterSpec snovaParameterSpec = (SnovaParameterSpec)algorithmParameterSpec;
            return snovaParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new SnovaKeyGenerationParameters(this.random, SnovaParameters.SNOVA_24_5_4_SSK);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        SnovaPublicKeyParameters snovaPublicKeyParameters = (SnovaPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        SnovaPrivateKeyParameters snovaPrivateKeyParameters = (SnovaPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCSnovaPublicKey(snovaPublicKeyParameters), new BCSnovaPrivateKey(snovaPrivateKeyParameters));
    }

    static {
        parameters.put("SNOVA_24_5_4_SSK", SnovaParameters.SNOVA_24_5_4_SSK);
        parameters.put("SNOVA_24_5_4_ESK", SnovaParameters.SNOVA_24_5_4_ESK);
        parameters.put("SNOVA_24_5_4_SHAKE_SSK", SnovaParameters.SNOVA_24_5_4_SHAKE_SSK);
        parameters.put("SNOVA_24_5_4_SHAKE_ESK", SnovaParameters.SNOVA_24_5_4_SHAKE_ESK);
        parameters.put("SNOVA_24_5_5_SSK", SnovaParameters.SNOVA_24_5_5_SSK);
        parameters.put("SNOVA_24_5_5_ESK", SnovaParameters.SNOVA_24_5_5_ESK);
        parameters.put("SNOVA_24_5_5_SHAKE_SSK", SnovaParameters.SNOVA_24_5_5_SHAKE_SSK);
        parameters.put("SNOVA_24_5_5_SHAKE_ESK", SnovaParameters.SNOVA_24_5_5_SHAKE_ESK);
        parameters.put("SNOVA_25_8_3_SSK", SnovaParameters.SNOVA_25_8_3_SSK);
        parameters.put("SNOVA_25_8_3_ESK", SnovaParameters.SNOVA_25_8_3_ESK);
        parameters.put("SNOVA_25_8_3_SHAKE_SSK", SnovaParameters.SNOVA_25_8_3_SHAKE_SSK);
        parameters.put("SNOVA_25_8_3_SHAKE_ESK", SnovaParameters.SNOVA_25_8_3_SHAKE_ESK);
        parameters.put("SNOVA_29_6_5_SSK", SnovaParameters.SNOVA_29_6_5_SSK);
        parameters.put("SNOVA_29_6_5_ESK", SnovaParameters.SNOVA_29_6_5_ESK);
        parameters.put("SNOVA_29_6_5_SHAKE_SSK", SnovaParameters.SNOVA_29_6_5_SHAKE_SSK);
        parameters.put("SNOVA_29_6_5_SHAKE_ESK", SnovaParameters.SNOVA_29_6_5_SHAKE_ESK);
        parameters.put("SNOVA_37_8_4_SSK", SnovaParameters.SNOVA_37_8_4_SSK);
        parameters.put("SNOVA_37_8_4_ESK", SnovaParameters.SNOVA_37_8_4_ESK);
        parameters.put("SNOVA_37_8_4_SHAKE_SSK", SnovaParameters.SNOVA_37_8_4_SHAKE_SSK);
        parameters.put("SNOVA_37_8_4_SHAKE_ESK", SnovaParameters.SNOVA_37_8_4_SHAKE_ESK);
        parameters.put("SNOVA_37_17_2_SSK", SnovaParameters.SNOVA_37_17_2_SSK);
        parameters.put("SNOVA_37_17_2_ESK", SnovaParameters.SNOVA_37_17_2_ESK);
        parameters.put("SNOVA_37_17_2_SHAKE_SSK", SnovaParameters.SNOVA_37_17_2_SHAKE_SSK);
        parameters.put("SNOVA_37_17_2_SHAKE_ESK", SnovaParameters.SNOVA_37_17_2_SHAKE_ESK);
        parameters.put("SNOVA_49_11_3_SSK", SnovaParameters.SNOVA_49_11_3_SSK);
        parameters.put("SNOVA_49_11_3_ESK", SnovaParameters.SNOVA_49_11_3_ESK);
        parameters.put("SNOVA_49_11_3_SHAKE_SSK", SnovaParameters.SNOVA_49_11_3_SHAKE_SSK);
        parameters.put("SNOVA_49_11_3_SHAKE_ESK", SnovaParameters.SNOVA_49_11_3_SHAKE_ESK);
        parameters.put("SNOVA_56_25_2_SSK", SnovaParameters.SNOVA_56_25_2_SSK);
        parameters.put("SNOVA_56_25_2_ESK", SnovaParameters.SNOVA_56_25_2_ESK);
        parameters.put("SNOVA_56_25_2_SHAKE_SSK", SnovaParameters.SNOVA_56_25_2_SHAKE_SSK);
        parameters.put("SNOVA_56_25_2_SHAKE_ESK", SnovaParameters.SNOVA_56_25_2_SHAKE_ESK);
        parameters.put("SNOVA_60_10_4_SSK", SnovaParameters.SNOVA_60_10_4_SSK);
        parameters.put("SNOVA_60_10_4_ESK", SnovaParameters.SNOVA_60_10_4_ESK);
        parameters.put("SNOVA_60_10_4_SHAKE_SSK", SnovaParameters.SNOVA_60_10_4_SHAKE_SSK);
        parameters.put("SNOVA_60_10_4_SHAKE_ESK", SnovaParameters.SNOVA_60_10_4_SHAKE_ESK);
        parameters.put("SNOVA_66_15_3_SSK", SnovaParameters.SNOVA_66_15_3_SSK);
        parameters.put("SNOVA_66_15_3_ESK", SnovaParameters.SNOVA_66_15_3_ESK);
        parameters.put("SNOVA_66_15_3_SHAKE_SSK", SnovaParameters.SNOVA_66_15_3_SHAKE_SSK);
        parameters.put("SNOVA_66_15_3_SHAKE_ESK", SnovaParameters.SNOVA_66_15_3_SHAKE_ESK);
        parameters.put("SNOVA_75_33_2_SSK", SnovaParameters.SNOVA_75_33_2_SSK);
        parameters.put("SNOVA_75_33_2_ESK", SnovaParameters.SNOVA_75_33_2_ESK);
        parameters.put("SNOVA_75_33_2_SHAKE_SSK", SnovaParameters.SNOVA_75_33_2_SHAKE_SSK);
        parameters.put("SNOVA_75_33_2_SHAKE_ESK", SnovaParameters.SNOVA_75_33_2_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_4_SSK.getName(), SnovaParameters.SNOVA_24_5_4_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_4_ESK.getName(), SnovaParameters.SNOVA_24_5_4_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_4_SHAKE_SSK.getName(), SnovaParameters.SNOVA_24_5_4_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_4_SHAKE_ESK.getName(), SnovaParameters.SNOVA_24_5_4_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_5_SSK.getName(), SnovaParameters.SNOVA_24_5_5_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_5_ESK.getName(), SnovaParameters.SNOVA_24_5_5_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_5_SHAKE_SSK.getName(), SnovaParameters.SNOVA_24_5_5_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_24_5_5_SHAKE_ESK.getName(), SnovaParameters.SNOVA_24_5_5_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_25_8_3_SSK.getName(), SnovaParameters.SNOVA_25_8_3_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_25_8_3_ESK.getName(), SnovaParameters.SNOVA_25_8_3_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_25_8_3_SHAKE_SSK.getName(), SnovaParameters.SNOVA_25_8_3_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_25_8_3_SHAKE_ESK.getName(), SnovaParameters.SNOVA_25_8_3_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_29_6_5_SSK.getName(), SnovaParameters.SNOVA_29_6_5_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_29_6_5_ESK.getName(), SnovaParameters.SNOVA_29_6_5_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_29_6_5_SHAKE_SSK.getName(), SnovaParameters.SNOVA_29_6_5_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_29_6_5_SHAKE_ESK.getName(), SnovaParameters.SNOVA_29_6_5_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_37_8_4_SSK.getName(), SnovaParameters.SNOVA_37_8_4_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_37_8_4_ESK.getName(), SnovaParameters.SNOVA_37_8_4_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_37_8_4_SHAKE_SSK.getName(), SnovaParameters.SNOVA_37_8_4_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_37_8_4_SHAKE_ESK.getName(), SnovaParameters.SNOVA_37_8_4_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_37_17_2_SSK.getName(), SnovaParameters.SNOVA_37_17_2_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_37_17_2_ESK.getName(), SnovaParameters.SNOVA_37_17_2_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_37_17_2_SHAKE_SSK.getName(), SnovaParameters.SNOVA_37_17_2_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_37_17_2_SHAKE_ESK.getName(), SnovaParameters.SNOVA_37_17_2_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_49_11_3_SSK.getName(), SnovaParameters.SNOVA_49_11_3_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_49_11_3_ESK.getName(), SnovaParameters.SNOVA_49_11_3_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_49_11_3_SHAKE_SSK.getName(), SnovaParameters.SNOVA_49_11_3_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_49_11_3_SHAKE_ESK.getName(), SnovaParameters.SNOVA_49_11_3_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_56_25_2_SSK.getName(), SnovaParameters.SNOVA_56_25_2_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_56_25_2_ESK.getName(), SnovaParameters.SNOVA_56_25_2_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_56_25_2_SHAKE_SSK.getName(), SnovaParameters.SNOVA_56_25_2_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_56_25_2_SHAKE_ESK.getName(), SnovaParameters.SNOVA_56_25_2_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_60_10_4_SSK.getName(), SnovaParameters.SNOVA_60_10_4_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_60_10_4_ESK.getName(), SnovaParameters.SNOVA_60_10_4_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_60_10_4_SHAKE_SSK.getName(), SnovaParameters.SNOVA_60_10_4_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_60_10_4_SHAKE_ESK.getName(), SnovaParameters.SNOVA_60_10_4_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_66_15_3_SSK.getName(), SnovaParameters.SNOVA_66_15_3_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_66_15_3_ESK.getName(), SnovaParameters.SNOVA_66_15_3_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_66_15_3_SHAKE_SSK.getName(), SnovaParameters.SNOVA_66_15_3_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_66_15_3_SHAKE_ESK.getName(), SnovaParameters.SNOVA_66_15_3_SHAKE_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_75_33_2_SSK.getName(), SnovaParameters.SNOVA_75_33_2_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_75_33_2_ESK.getName(), SnovaParameters.SNOVA_75_33_2_ESK);
        parameters.put(SnovaParameterSpec.SNOVA_75_33_2_SHAKE_SSK.getName(), SnovaParameters.SNOVA_75_33_2_SHAKE_SSK);
        parameters.put(SnovaParameterSpec.SNOVA_75_33_2_SHAKE_ESK.getName(), SnovaParameters.SNOVA_75_33_2_SHAKE_ESK);
    }

    public static class SNOVA_24_5_4_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_4_ESK() {
            super(SnovaParameters.SNOVA_24_5_4_ESK);
        }
    }

    public static class SNOVA_24_5_4_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_4_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_24_5_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_24_5_4_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_4_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_24_5_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_24_5_4_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_4_SSK() {
            super(SnovaParameters.SNOVA_24_5_4_SSK);
        }
    }

    public static class SNOVA_24_5_5_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_5_ESK() {
            super(SnovaParameters.SNOVA_24_5_5_ESK);
        }
    }

    public static class SNOVA_24_5_5_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_5_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_24_5_5_SHAKE_ESK);
        }
    }

    public static class SNOVA_24_5_5_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_5_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_24_5_5_SHAKE_SSK);
        }
    }

    public static class SNOVA_24_5_5_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_24_5_5_SSK() {
            super(SnovaParameters.SNOVA_24_5_5_SSK);
        }
    }

    public static class SNOVA_25_8_3_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_25_8_3_ESK() {
            super(SnovaParameters.SNOVA_25_8_3_ESK);
        }
    }

    public static class SNOVA_25_8_3_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_25_8_3_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_25_8_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_25_8_3_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_25_8_3_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_25_8_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_25_8_3_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_25_8_3_SSK() {
            super(SnovaParameters.SNOVA_25_8_3_SSK);
        }
    }

    public static class SNOVA_29_6_5_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_29_6_5_ESK() {
            super(SnovaParameters.SNOVA_29_6_5_ESK);
        }
    }

    public static class SNOVA_29_6_5_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_29_6_5_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_29_6_5_SHAKE_ESK);
        }
    }

    public static class SNOVA_29_6_5_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_29_6_5_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_29_6_5_SHAKE_SSK);
        }
    }

    public static class SNOVA_29_6_5_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_29_6_5_SSK() {
            super(SnovaParameters.SNOVA_29_6_5_SSK);
        }
    }

    public static class SNOVA_37_17_2_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_17_2_ESK() {
            super(SnovaParameters.SNOVA_37_17_2_ESK);
        }
    }

    public static class SNOVA_37_17_2_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_17_2_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_37_17_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_37_17_2_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_17_2_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_37_17_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_37_17_2_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_17_2_SSK() {
            super(SnovaParameters.SNOVA_37_17_2_SSK);
        }
    }

    public static class SNOVA_37_8_4_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_8_4_ESK() {
            super(SnovaParameters.SNOVA_37_8_4_ESK);
        }
    }

    public static class SNOVA_37_8_4_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_8_4_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_37_8_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_37_8_4_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_8_4_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_37_8_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_37_8_4_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_37_8_4_SSK() {
            super(SnovaParameters.SNOVA_37_8_4_SSK);
        }
    }

    public static class SNOVA_49_11_3_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_49_11_3_ESK() {
            super(SnovaParameters.SNOVA_49_11_3_ESK);
        }
    }

    public static class SNOVA_49_11_3_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_49_11_3_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_49_11_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_49_11_3_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_49_11_3_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_49_11_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_49_11_3_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_49_11_3_SSK() {
            super(SnovaParameters.SNOVA_49_11_3_SSK);
        }
    }

    public static class SNOVA_56_25_2_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_56_25_2_ESK() {
            super(SnovaParameters.SNOVA_56_25_2_ESK);
        }
    }

    public static class SNOVA_56_25_2_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_56_25_2_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_56_25_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_56_25_2_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_56_25_2_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_56_25_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_56_25_2_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_56_25_2_SSK() {
            super(SnovaParameters.SNOVA_56_25_2_SSK);
        }
    }

    public static class SNOVA_60_10_4_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_60_10_4_ESK() {
            super(SnovaParameters.SNOVA_60_10_4_ESK);
        }
    }

    public static class SNOVA_60_10_4_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_60_10_4_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_60_10_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_60_10_4_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_60_10_4_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_60_10_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_60_10_4_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_60_10_4_SSK() {
            super(SnovaParameters.SNOVA_60_10_4_SSK);
        }
    }

    public static class SNOVA_66_15_3_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_66_15_3_ESK() {
            super(SnovaParameters.SNOVA_66_15_3_ESK);
        }
    }

    public static class SNOVA_66_15_3_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_66_15_3_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_66_15_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_66_15_3_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_66_15_3_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_66_15_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_66_15_3_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_66_15_3_SSK() {
            super(SnovaParameters.SNOVA_66_15_3_SSK);
        }
    }

    public static class SNOVA_75_33_2_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_75_33_2_ESK() {
            super(SnovaParameters.SNOVA_75_33_2_ESK);
        }
    }

    public static class SNOVA_75_33_2_SHAKE_ESK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_75_33_2_SHAKE_ESK() {
            super(SnovaParameters.SNOVA_75_33_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_75_33_2_SHAKE_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_75_33_2_SHAKE_SSK() {
            super(SnovaParameters.SNOVA_75_33_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_75_33_2_SSK
    extends SnovaKeyPairGeneratorSpi {
        public SNOVA_75_33_2_SSK() {
            super(SnovaParameters.SNOVA_75_33_2_SSK);
        }
    }
}

