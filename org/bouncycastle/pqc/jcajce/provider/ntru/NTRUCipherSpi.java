/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.jcajce.spec.KEMParameterSpec;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class NTRUCipherSpi
extends CipherSpi {
    private final String algorithmName;
    private NTRUKEMGenerator kemGen;
    private KTSParameterSpec kemParameterSpec;
    private BCNTRUPublicKey wrapKey;
    private BCNTRUPrivateKey unwrapKey;
    private AlgorithmParameters engineParams;

    NTRUCipherSpi(String string) throws NoSuchAlgorithmException {
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
            if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
                throw new InvalidAlgorithmParameterException(this.algorithmName + " can only accept KTSParameterSpec");
            }
            this.kemParameterSpec = (KTSParameterSpec)algorithmParameterSpec;
        }
        if (n == 3) {
            if (!(key instanceof BCNTRUPublicKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
            this.wrapKey = (BCNTRUPublicKey)key;
            this.kemGen = new NTRUKEMGenerator(CryptoServicesRegistrar.getSecureRandom(secureRandom));
            return;
        } else {
            if (n != 4) throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
            if (!(key instanceof BCNTRUPrivateKey)) throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
            this.unwrapKey = (BCNTRUPrivateKey)key;
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
                NTRUKEMExtractor nTRUKEMExtractor = new NTRUKEMExtractor(this.unwrapKey.getKeyParams());
                byArray2 = nTRUKEMExtractor.extractSecret(Arrays.copyOfRange(byArray, 0, nTRUKEMExtractor.getEncapsulationLength()));
                Wrapper wrapper = WrapUtil.getKeyUnwrapper(this.kemParameterSpec, byArray2);
                byte[] byArray3 = Arrays.copyOfRange(byArray, nTRUKEMExtractor.getEncapsulationLength(), byArray.length);
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
    extends NTRUCipherSpi {
        public Base() throws NoSuchAlgorithmException {
            super("NTRU");
        }
    }
}

