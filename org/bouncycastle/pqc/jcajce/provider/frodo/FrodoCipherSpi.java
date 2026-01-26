/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.frodo;

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
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMParameterSpec;
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMExtractor;
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.frodo.BCFrodoPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.frodo.BCFrodoPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class FrodoCipherSpi
extends CipherSpi {
    private final String algorithmName;
    private FrodoKEMGenerator kemGen;
    private KEMParameterSpec kemParameterSpec;
    private BCFrodoPublicKey wrapKey;
    private BCFrodoPrivateKey unwrapKey;
    private AlgorithmParameters engineParams;

    FrodoCipherSpi(String string) throws NoSuchAlgorithmException {
        this.algorithmName = string;
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
        if (algorithmParameterSpec == null) {
            this.kemParameterSpec = new KEMParameterSpec("AES-KWP");
        } else {
            if (!(algorithmParameterSpec instanceof KEMParameterSpec)) {
                throw new InvalidAlgorithmParameterException(this.algorithmName + " can only accept KTSParameterSpec");
            }
            this.kemParameterSpec = (KEMParameterSpec)algorithmParameterSpec;
        }
        if (n == 3) {
            if (!(key instanceof BCFrodoPublicKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
            this.wrapKey = (BCFrodoPublicKey)key;
            this.kemGen = new FrodoKEMGenerator(CryptoServicesRegistrar.getSecureRandom(secureRandom));
            return;
        } else {
            if (n != 4) throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
            if (!(key instanceof BCFrodoPrivateKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
            this.unwrapKey = (BCFrodoPrivateKey)key;
        }
    }

    @Override
    protected void engineInit(int n, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        KEMParameterSpec kEMParameterSpec = null;
        if (algorithmParameters != null) {
            try {
                kEMParameterSpec = algorithmParameters.getParameterSpec(KEMParameterSpec.class);
            }
            catch (Exception exception) {
                throw new InvalidAlgorithmParameterException("can't handle parameter " + algorithmParameters.toString());
            }
        }
        this.engineInit(n, key, kEMParameterSpec, secureRandom);
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
        try {
            SecretWithEncapsulation secretWithEncapsulation = this.kemGen.generateEncapsulated(this.wrapKey.getKeyParams());
            Wrapper wrapper = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            KeyParameter keyParameter = new KeyParameter(secretWithEncapsulation.getSecret());
            wrapper.init(true, keyParameter);
            byte[] byArray2 = secretWithEncapsulation.getEncapsulation();
            secretWithEncapsulation.destroy();
            byte[] byArray3 = key.getEncoded();
            byte[] byArray4 = Arrays.concatenate(byArray2, wrapper.wrap(byArray3, 0, byArray3.length));
            Arrays.clear(byArray3);
            return byArray4;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalBlockSizeException("unable to generate KTS secret: " + illegalArgumentException.getMessage());
        }
        catch (DestroyFailedException destroyFailedException) {
            throw new IllegalBlockSizeException("unable to destroy interim values: " + destroyFailedException.getMessage());
        }
    }

    @Override
    protected Key engineUnwrap(byte[] byArray, String string, int n) throws InvalidKeyException, NoSuchAlgorithmException {
        if (n != 3) {
            throw new InvalidKeyException("only SECRET_KEY supported");
        }
        try {
            FrodoKEMExtractor frodoKEMExtractor = new FrodoKEMExtractor(this.unwrapKey.getKeyParams());
            byte[] byArray2 = frodoKEMExtractor.extractSecret(Arrays.copyOfRange(byArray, 0, frodoKEMExtractor.getEncapsulationLength()));
            Wrapper wrapper = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            KeyParameter keyParameter = new KeyParameter(byArray2);
            Arrays.clear(byArray2);
            wrapper.init(false, keyParameter);
            byte[] byArray3 = Arrays.copyOfRange(byArray, frodoKEMExtractor.getEncapsulationLength(), byArray.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(wrapper.unwrap(byArray3, 0, byArray3.length), string);
            Arrays.clear(keyParameter.getKey());
            return secretKeySpec;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new NoSuchAlgorithmException("unable to extract KTS secret: " + illegalArgumentException.getMessage());
        }
        catch (InvalidCipherTextException invalidCipherTextException) {
            throw new InvalidKeyException("unable to extract KTS secret: " + invalidCipherTextException.getMessage());
        }
    }

    public static class Base
    extends FrodoCipherSpi {
        public Base() throws NoSuchAlgorithmException {
            super("Frodo");
        }
    }
}

