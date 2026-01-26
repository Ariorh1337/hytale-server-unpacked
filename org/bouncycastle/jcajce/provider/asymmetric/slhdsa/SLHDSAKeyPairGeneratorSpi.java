/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.asymmetric.slhdsa.BCSLHDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.slhdsa.BCSLHDSAPublicKey;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class SLHDSAKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    SLHDSAKeyGenerationParameters param;
    SLHDSAKeyPairGenerator engine = new SLHDSAKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public SLHDSAKeyPairGeneratorSpi(String string) {
        super(string);
    }

    protected SLHDSAKeyPairGeneratorSpi(SLHDSAParameterSpec sLHDSAParameterSpec) {
        super("SLH-DSA-" + Strings.toUpperCase(sLHDSAParameterSpec.getName()));
        this.param = new SLHDSAKeyGenerationParameters(this.random, (SLHDSAParameters)parameters.get(sLHDSAParameterSpec.getName()));
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        SLHDSAParameters sLHDSAParameters;
        String string = SLHDSAKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null) {
            sLHDSAParameters = (SLHDSAParameters)parameters.get(string);
            if (sLHDSAParameters == null) {
                throw new InvalidAlgorithmParameterException("unknown parameter set name: " + string);
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new SLHDSAKeyGenerationParameters(secureRandom, sLHDSAParameters);
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = this.getAlgorithm().startsWith("HASH") ? new SLHDSAKeyGenerationParameters(this.random, SLHDSAParameters.sha2_128f_with_sha256) : new SLHDSAKeyGenerationParameters(this.random, SLHDSAParameters.sha2_128f);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters = (SLHDSAPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        SLHDSAPrivateKeyParameters sLHDSAPrivateKeyParameters = (SLHDSAPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCSLHDSAPublicKey(sLHDSAPublicKeyParameters), new BCSLHDSAPrivateKey(sLHDSAPrivateKeyParameters));
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof SLHDSAParameterSpec) {
            SLHDSAParameterSpec sLHDSAParameterSpec = (SLHDSAParameterSpec)algorithmParameterSpec;
            return sLHDSAParameterSpec.getName();
        }
        return Strings.toUpperCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    static {
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128f.getName(), SLHDSAParameters.sha2_128f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128s.getName(), SLHDSAParameters.sha2_128s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192f.getName(), SLHDSAParameters.sha2_192f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192s.getName(), SLHDSAParameters.sha2_192s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256f.getName(), SLHDSAParameters.sha2_256f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256s.getName(), SLHDSAParameters.sha2_256s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128f.getName(), SLHDSAParameters.shake_128f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128s.getName(), SLHDSAParameters.shake_128s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192f.getName(), SLHDSAParameters.shake_192f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192s.getName(), SLHDSAParameters.shake_192s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256f.getName(), SLHDSAParameters.shake_256f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256s.getName(), SLHDSAParameters.shake_256s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256.getName(), SLHDSAParameters.sha2_128f_with_sha256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256.getName(), SLHDSAParameters.sha2_128s_with_sha256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512.getName(), SLHDSAParameters.sha2_192f_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512.getName(), SLHDSAParameters.sha2_192s_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512.getName(), SLHDSAParameters.sha2_256f_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512.getName(), SLHDSAParameters.sha2_256s_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128.getName(), SLHDSAParameters.shake_128f_with_shake128);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128.getName(), SLHDSAParameters.shake_128s_with_shake128);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256.getName(), SLHDSAParameters.shake_192f_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256.getName(), SLHDSAParameters.shake_192s_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256.getName(), SLHDSAParameters.shake_256f_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256.getName(), SLHDSAParameters.shake_256s_with_shake256);
    }

    public static class Hash
    extends SLHDSAKeyPairGeneratorSpi {
        public Hash() throws NoSuchAlgorithmException {
            super("HASH-SLH-DSA");
        }
    }

    public static class HashSha2_128f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_128f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256);
        }
    }

    public static class HashSha2_128s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_128s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256);
        }
    }

    public static class HashSha2_192f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_192f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512);
        }
    }

    public static class HashSha2_192s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_192s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512);
        }
    }

    public static class HashSha2_256f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_256f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512);
        }
    }

    public static class HashSha2_256s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_256s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512);
        }
    }

    public static class HashShake_128f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_128f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128);
        }
    }

    public static class HashShake_128s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_128s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128);
        }
    }

    public static class HashShake_192f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_192f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256);
        }
    }

    public static class HashShake_192s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_192s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256);
        }
    }

    public static class HashShake_256f
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_256f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256);
        }
    }

    public static class HashShake_256s
    extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_256s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256);
        }
    }

    public static class Pure
    extends SLHDSAKeyPairGeneratorSpi {
        public Pure() throws NoSuchAlgorithmException {
            super("SLH-DSA");
        }
    }

    public static class Sha2_128f
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_128f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128f);
        }
    }

    public static class Sha2_128s
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_128s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128s);
        }
    }

    public static class Sha2_192f
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_192f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192f);
        }
    }

    public static class Sha2_192s
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_192s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192s);
        }
    }

    public static class Sha2_256f
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_256f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256f);
        }
    }

    public static class Sha2_256s
    extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_256s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256s);
        }
    }

    public static class Shake_128f
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_128f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128f);
        }
    }

    public static class Shake_128s
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_128s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128s);
        }
    }

    public static class Shake_192f
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_192f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192f);
        }
    }

    public static class Shake_192s
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_192s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192s);
        }
    }

    public static class Shake_256f
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_256f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256f);
        }
    }

    public static class Shake_256s
    extends SLHDSAKeyPairGeneratorSpi {
        public Shake_256s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256s);
        }
    }
}

