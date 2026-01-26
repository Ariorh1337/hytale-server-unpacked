/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class MLKEMCipherSpi
extends CipherSpi {
    private final String algorithmName;
    private MLKEMGenerator kemGen;
    private KTSParameterSpec kemParameterSpec;
    private BCMLKEMPublicKey wrapKey;
    private BCMLKEMPrivateKey unwrapKey;
    private AlgorithmParameters engineParams;
    private MLKEMParameters mlkemParamters;

    MLKEMCipherSpi(String string) {
        this.algorithmName = string;
        this.mlkemParamters = null;
    }

    MLKEMCipherSpi(MLKEMParameters mLKEMParameters) {
        this.mlkemParamters = mLKEMParameters;
        this.algorithmName = mLKEMParameters.getName();
    }

    @Override
    protected void engineSetMode(String string) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Cannot support mode " + string);
    }

    @Override
    protected void engineSetPadding(String string) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + string + " unknown");
    }

    @Override
    protected int engineGetKeySize(Key key) {
        return 2048;
    }

    @Override
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override
    protected int engineGetOutputSize(int n) {
        return -1;
    }

    @Override
    protected byte[] engineGetIV() {
        return null;
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            try {
                this.engineParams = AlgorithmParameters.getInstance(this.algorithmName, "BCPQC");
                this.engineParams.init(this.kemParameterSpec);
            }
            catch (Exception exception) {
                throw Exceptions.illegalStateException(exception.toString(), exception);
            }
        }
        return this.engineParams;
    }

    @Override
    protected void engineInit(int n, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw Exceptions.illegalArgumentException(invalidAlgorithmParameterException.getMessage(), invalidAlgorithmParameterException);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineInit(int n, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        String string;
        if (algorithmParameterSpec == null) {
            this.kemParameterSpec = new KTSParameterSpec.Builder("AES-KWP", 256).build();
        } else {
            if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
                throw new InvalidAlgorithmParameterException(this.algorithmName + " can only accept KTSParameterSpec");
            }
            this.kemParameterSpec = (KTSParameterSpec)algorithmParameterSpec;
        }
        if (n == 3) {
            if (!(key instanceof BCMLKEMPublicKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
            this.wrapKey = (BCMLKEMPublicKey)key;
            this.kemGen = new MLKEMGenerator(CryptoServicesRegistrar.getSecureRandom(secureRandom));
        } else {
            if (n != 4) throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
            if (!(key instanceof BCMLKEMPrivateKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
            this.unwrapKey = (BCMLKEMPrivateKey)key;
        }
        if (this.mlkemParamters == null || (string = MLKEMParameterSpec.fromName(this.mlkemParamters.getName()).getName()).equals(key.getAlgorithm())) return;
        throw new InvalidKeyException("cipher locked to " + string);
    }

    @Override
    protected void engineInit(int n, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        KTSParameterSpec kTSParameterSpec = null;
        if (algorithmParameters != null) {
            try {
                kTSParameterSpec = algorithmParameters.getParameterSpec(KTSParameterSpec.class);
            }
            catch (Exception exception) {
                throw new InvalidAlgorithmParameterException("can't handle parameter " + algorithmParameters.toString());
            }
        }
        this.engineInit(n, key, kTSParameterSpec, secureRandom);
    }

    @Override
    protected byte[] engineUpdate(byte[] byArray, int n, int n2) {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override
    protected int engineUpdate(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override
    protected byte[] engineDoFinal(byte[] byArray, int n, int n2) throws IllegalBlockSizeException, BadPaddingException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override
    protected int engineDoFinal(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] byArray = key.getEncoded();
        if (byArray == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        SecretWithEncapsulation secretWithEncapsulation = null;
        try {
            secretWithEncapsulation = this.kemGen.generateEncapsulated(this.wrapKey.getKeyParams());
            Wrapper wrapper = WrapUtil.getKeyWrapper(this.kemParameterSpec, secretWithEncapsulation.getSecret());
            byte[] byArray2 = secretWithEncapsulation.getEncapsulation();
            byte[] byArray3 = key.getEncoded();
            byte[] byArray4 = Arrays.concatenate(byArray2, wrapper.wrap(byArray3, 0, byArray3.length));
            Arrays.clear(byArray3);
            byte[] byArray5 = byArray4;
            return byArray5;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalBlockSizeException("unable to generate KTS secret: " + illegalArgumentException.getMessage());
        }
        finally {
            try {
                if (secretWithEncapsulation != null) {
                    secretWithEncapsulation.destroy();
                }
            }
            catch (DestroyFailedException destroyFailedException) {
                throw new IllegalBlockSizeException("unable to destroy interim values: " + destroyFailedException.getMessage());
            }
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Key engineUnwrap(byte[] byArray, String string, int n) throws InvalidKeyException, NoSuchAlgorithmException {
        SecretKeySpec secretKeySpec;
        block7: {
            if (n != 3) {
                throw new InvalidKeyException("only SECRET_KEY supported");
            }
            byte[] byArray2 = null;
            try {
                SecretKeySpec secretKeySpec2;
                MLKEMExtractor mLKEMExtractor = new MLKEMExtractor(this.unwrapKey.getKeyParams());
                byArray2 = mLKEMExtractor.extractSecret(Arrays.copyOfRange(byArray, 0, mLKEMExtractor.getEncapsulationLength()));
                Wrapper wrapper = WrapUtil.getKeyUnwrapper(this.kemParameterSpec, byArray2);
                byte[] byArray3 = Arrays.copyOfRange(byArray, mLKEMExtractor.getEncapsulationLength(), byArray.length);
                secretKeySpec = secretKeySpec2 = new SecretKeySpec(wrapper.unwrap(byArray3, 0, byArray3.length), string);
                if (byArray2 == null) break block7;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                try {
                    throw new NoSuchAlgorithmException("unable to extract KTS secret: " + illegalArgumentException.getMessage());
                    catch (InvalidCipherTextException invalidCipherTextException) {
                        throw new InvalidKeyException("unable to extract KTS secret: " + invalidCipherTextException.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    if (byArray2 != null) {
                        Arrays.clear(byArray2);
                    }
                    throw throwable;
                }
            }
            Arrays.clear(byArray2);
        }
        return secretKeySpec;
    }

    public static class Base
    extends MLKEMCipherSpi {
        public Base() throws NoSuchAlgorithmException {
            super("MLKEM");
        }
    }

    public static class MLKEM1024
    extends MLKEMCipherSpi {
        public MLKEM1024() {
            super(MLKEMParameters.ml_kem_1024);
        }
    }

    public static class MLKEM512
    extends MLKEMCipherSpi {
        public MLKEM512() {
            super(MLKEMParameters.ml_kem_512);
        }
    }

    public static class MLKEM768
    extends MLKEMCipherSpi {
        public MLKEM768() {
            super(MLKEMParameters.ml_kem_768);
        }
    }
}

