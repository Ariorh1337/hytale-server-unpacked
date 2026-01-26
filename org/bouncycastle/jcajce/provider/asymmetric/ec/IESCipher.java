/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.parsers.ECIESPublicKeyParser;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.IESUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.IESKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class IESCipher
extends BaseCipherSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private int ivLength;
    private IESEngine engine;
    private int state = -1;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AlgorithmParameters engineParam = null;
    private IESParameterSpec engineSpec = null;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode = false;
    private AsymmetricKeyParameter otherKeyParameter = null;

    public IESCipher(IESEngine iESEngine) {
        this.engine = iESEngine;
        this.ivLength = 0;
    }

    public IESCipher(IESEngine iESEngine, int n) {
        this.engine = iESEngine;
        this.ivLength = n;
    }

    @Override
    public int engineGetBlockSize() {
        BufferedBlockCipher bufferedBlockCipher = this.engine.getCipher();
        return bufferedBlockCipher == null ? 0 : bufferedBlockCipher.getBlockSize();
    }

    @Override
    public int engineGetKeySize(Key key) {
        if (key instanceof ECKey) {
            return ((ECKey)((Object)key)).getParameters().getCurve().getFieldSize();
        }
        throw new IllegalArgumentException("not an EC key");
    }

    @Override
    public byte[] engineGetIV() {
        if (this.engineSpec != null) {
            return this.engineSpec.getNonce();
        }
        return null;
    }

    @Override
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParam == null && this.engineSpec != null) {
            try {
                this.engineParam = this.helper.createAlgorithmParameters("IES");
                this.engineParam.init(this.engineSpec);
            }
            catch (Exception exception) {
                throw new RuntimeException(exception.toString());
            }
        }
        return this.engineParam;
    }

    @Override
    public void engineSetMode(String string) throws NoSuchAlgorithmException {
        String string2 = Strings.toUpperCase(string);
        if (string2.equals("NONE")) {
            this.dhaesMode = false;
        } else if (string2.equals("DHAES")) {
            this.dhaesMode = true;
        } else {
            throw new IllegalArgumentException("can't support mode " + string);
        }
    }

    @Override
    public int engineGetOutputSize(int n) {
        int n2;
        int n3;
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        int n4 = this.engine.getMac().getMacSize();
        if (this.otherKeyParameter == null) {
            ECCurve eCCurve = ((ECKeyParameters)this.key).getParameters().getCurve();
            int n5 = (eCCurve.getFieldSize() + 7) / 8;
            n3 = 1 + 2 * n5;
        } else {
            n3 = 0;
        }
        int n6 = this.buffer.size() + n;
        if (this.engine.getCipher() == null) {
            n2 = this.state == 2 || this.state == 4 ? n6 - n4 - n3 : n6;
        } else if (this.state == 1 || this.state == 3) {
            n2 = this.engine.getCipher().getOutputSize(n6);
        } else if (this.state == 2 || this.state == 4) {
            n2 = this.engine.getCipher().getOutputSize(n6 - n4 - n3);
        } else {
            throw new IllegalStateException("cipher not initialised");
        }
        if (this.state == 1 || this.state == 3) {
            return n4 + n3 + n2;
        }
        if (this.state == 2 || this.state == 4) {
            return n2;
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public void engineSetPadding(String string) throws NoSuchPaddingException {
        String string2 = Strings.toUpperCase(string);
        if (!(string2.equals("NOPADDING") || string2.equals("PKCS5PADDING") || string2.equals("PKCS7PADDING"))) {
            throw new NoSuchPaddingException("padding not available with IESCipher");
        }
    }

    @Override
    public void engineInit(int n, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        IESParameterSpec iESParameterSpec = null;
        if (algorithmParameters != null) {
            try {
                iESParameterSpec = algorithmParameters.getParameterSpec(IESParameterSpec.class);
            }
            catch (Exception exception) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + exception.toString());
            }
        }
        this.engineParam = algorithmParameters;
        this.engineInit(n, key, iESParameterSpec, secureRandom);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void engineInit(int n, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        this.otherKeyParameter = null;
        if (algorithmParameterSpec == null && this.ivLength == 0) {
            this.engineSpec = IESUtil.guessParameterSpec(this.engine.getCipher(), null);
        } else {
            if (!(algorithmParameterSpec instanceof IESParameterSpec)) throw new InvalidAlgorithmParameterException("must be passed IES parameters");
            this.engineSpec = (IESParameterSpec)algorithmParameterSpec;
        }
        byte[] byArray = this.engineSpec.getNonce();
        if (this.ivLength != 0 && (byArray == null || byArray.length != this.ivLength)) {
            throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
        }
        if (n == 1 || n == 3) {
            if (key instanceof PublicKey) {
                this.key = ECUtils.generatePublicKeyParameter((PublicKey)key);
            } else {
                if (!(key instanceof IESKey)) throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
                IESKey iESKey = (IESKey)key;
                this.key = ECUtils.generatePublicKeyParameter(iESKey.getPublic());
                this.otherKeyParameter = ECUtils.generatePrivateKeyParameter(iESKey.getPrivate());
            }
        } else {
            if (n != 2 && n != 4) throw new InvalidKeyException("must be passed EC key");
            if (key instanceof PrivateKey) {
                this.key = ECUtils.generatePrivateKeyParameter((PrivateKey)key);
            } else {
                if (!(key instanceof IESKey)) throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
                IESKey iESKey = (IESKey)key;
                this.otherKeyParameter = ECUtils.generatePublicKeyParameter(iESKey.getPublic());
                this.key = ECUtils.generatePrivateKeyParameter(iESKey.getPrivate());
            }
        }
        this.random = secureRandom;
        this.state = n;
        this.buffer.reset();
    }

    @Override
    public void engineInit(int n, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw new IllegalArgumentException("cannot handle supplied parameter spec: " + invalidAlgorithmParameterException.getMessage());
        }
    }

    @Override
    public byte[] engineUpdate(byte[] byArray, int n, int n2) {
        this.buffer.write(byArray, n, n2);
        return null;
    }

    @Override
    public int engineUpdate(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        this.buffer.write(byArray, n, n2);
        return 0;
    }

    @Override
    public byte[] engineDoFinal(byte[] byArray, int n, int n2) throws IllegalBlockSizeException, BadPaddingException {
        if (n2 != 0) {
            this.buffer.write(byArray, n, n2);
        }
        byte[] byArray2 = this.buffer.toByteArray();
        this.buffer.reset();
        CipherParameters cipherParameters = new IESWithCipherParameters(this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize());
        byte[] byArray3 = this.engineSpec.getNonce();
        if (byArray3 != null) {
            cipherParameters = new ParametersWithIV(cipherParameters, byArray3);
        }
        ECDomainParameters eCDomainParameters = ((ECKeyParameters)this.key).getParameters();
        if (this.otherKeyParameter != null) {
            try {
                if (this.state == 1 || this.state == 3) {
                    this.engine.init(true, this.otherKeyParameter, this.key, cipherParameters);
                } else {
                    this.engine.init(false, this.key, this.otherKeyParameter, cipherParameters);
                }
                return this.engine.processBlock(byArray2, 0, byArray2.length);
            }
            catch (Exception exception) {
                throw new BadBlockException("unable to process block", exception);
            }
        }
        if (this.state == 1 || this.state == 3) {
            ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
            eCKeyPairGenerator.init(new ECKeyGenerationParameters(eCDomainParameters, this.random));
            final boolean bl = this.engineSpec.getPointCompression();
            EphemeralKeyPairGenerator ephemeralKeyPairGenerator = new EphemeralKeyPairGenerator(eCKeyPairGenerator, new KeyEncoder(){
                final /* synthetic */ IESCipher this$0;
                {
                    this.this$0 = iESCipher;
                }

                @Override
                public byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
                    return ((ECPublicKeyParameters)asymmetricKeyParameter).getQ().getEncoded(bl);
                }
            });
            try {
                this.engine.init(this.key, cipherParameters, ephemeralKeyPairGenerator);
                return this.engine.processBlock(byArray2, 0, byArray2.length);
            }
            catch (Exception exception) {
                throw new BadBlockException("unable to process block", exception);
            }
        }
        if (this.state == 2 || this.state == 4) {
            try {
                this.engine.init(this.key, cipherParameters, new ECIESPublicKeyParser(eCDomainParameters));
                return this.engine.processBlock(byArray2, 0, byArray2.length);
            }
            catch (InvalidCipherTextException invalidCipherTextException) {
                throw new BadBlockException("unable to process block", invalidCipherTextException);
            }
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public int engineDoFinal(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] byArray3 = this.engineDoFinal(byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, byArray3.length);
        return byArray3.length;
    }

    public static class ECIES
    extends IESCipher {
        public ECIES() {
            this(DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public ECIES(Digest digest, Digest digest2) {
            super(new IESEngine(new ECDHBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2)));
        }
    }

    public static class ECIESwithAESCBC
    extends ECIESwithCipher {
        public ECIESwithAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16);
        }
    }

    public static class ECIESwithCipher
    extends IESCipher {
        public ECIESwithCipher(BlockCipher blockCipher, int n) {
            this(blockCipher, n, DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public ECIESwithCipher(BlockCipher blockCipher, int n, Digest digest, Digest digest2) {
            super(new IESEngine(new ECDHBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2), new PaddedBufferedBlockCipher(blockCipher)), n);
        }
    }

    public static class ECIESwithDESedeCBC
    extends ECIESwithCipher {
        public ECIESwithDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8);
        }
    }

    public static class ECIESwithSHA256
    extends ECIES {
        public ECIESwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class ECIESwithSHA256andAESCBC
    extends ECIESwithCipher {
        public ECIESwithSHA256andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class ECIESwithSHA256andDESedeCBC
    extends ECIESwithCipher {
        public ECIESwithSHA256andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class ECIESwithSHA384
    extends ECIES {
        public ECIESwithSHA384() {
            super(DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class ECIESwithSHA384andAESCBC
    extends ECIESwithCipher {
        public ECIESwithSHA384andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class ECIESwithSHA384andDESedeCBC
    extends ECIESwithCipher {
        public ECIESwithSHA384andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class ECIESwithSHA512
    extends ECIES {
        public ECIESwithSHA512() {
            super(DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    public static class ECIESwithSHA512andAESCBC
    extends ECIESwithCipher {
        public ECIESwithSHA512andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    public static class ECIESwithSHA512andDESedeCBC
    extends ECIESwithCipher {
        public ECIESwithSHA512andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }
}

