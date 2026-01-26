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
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class IESKEMCipher
extends BaseCipherSpi {
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final ECDHCBasicAgreement agreement;
    private final KDF2BytesGenerator kdf;
    private final Mac hMac;
    private final int macKeyLength;
    private final int macLength;
    private int ivLength;
    private IESEngine engine;
    private int state = -1;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AlgorithmParameters engineParam = null;
    private IESKEMParameterSpec engineSpec = null;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode = false;
    private AsymmetricKeyParameter otherKeyParameter = null;

    public IESKEMCipher(ECDHCBasicAgreement eCDHCBasicAgreement, KDF2BytesGenerator kDF2BytesGenerator, Mac mac, int n, int n2) {
        this.agreement = eCDHCBasicAgreement;
        this.kdf = kDF2BytesGenerator;
        this.hMac = mac;
        this.macKeyLength = n;
        this.macLength = n2;
    }

    @Override
    public int engineGetBlockSize() {
        return 0;
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
        throw new NoSuchAlgorithmException("can't support mode " + string);
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
            n3 = 2 * n5;
        } else {
            n3 = 0;
        }
        int n6 = this.buffer.size() + n;
        if (this.engine.getCipher() == null) {
            n2 = n6;
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
        throw new NoSuchPaddingException("padding not available with IESCipher");
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
        this.engineSpec = (IESKEMParameterSpec)algorithmParameterSpec;
        if (n == 1 || n == 3) {
            if (!(key instanceof PublicKey)) throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
            this.key = ECUtils.generatePublicKeyParameter((PublicKey)key);
        } else {
            if (n != 2 && n != 4) throw new InvalidKeyException("must be passed EC key");
            if (!(key instanceof PrivateKey)) throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
            this.key = ECUtils.generatePrivateKeyParameter((PrivateKey)key);
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
        ECDomainParameters eCDomainParameters = ((ECKeyParameters)this.key).getParameters();
        if (this.state == 1 || this.state == 3) {
            ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
            eCKeyPairGenerator.init(new ECKeyGenerationParameters(eCDomainParameters, this.random));
            final boolean bl = this.engineSpec.hasUsePointCompression();
            EphemeralKeyPairGenerator ephemeralKeyPairGenerator = new EphemeralKeyPairGenerator(eCKeyPairGenerator, new KeyEncoder(){
                final /* synthetic */ IESKEMCipher this$0;
                {
                    this.this$0 = iESKEMCipher;
                }

                @Override
                public byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
                    return ((ECPublicKeyParameters)asymmetricKeyParameter).getQ().getEncoded(bl);
                }
            });
            EphemeralKeyPair ephemeralKeyPair = ephemeralKeyPairGenerator.generate();
            this.agreement.init(ephemeralKeyPair.getKeyPair().getPrivate());
            byte[] byArray3 = converter.integerToBytes(this.agreement.calculateAgreement(this.key), converter.getByteLength(eCDomainParameters.getCurve()));
            byte[] byArray4 = new byte[n2 + this.macKeyLength];
            this.kdf.init(new KDFParameters(byArray3, this.engineSpec.getRecipientInfo()));
            this.kdf.generateBytes(byArray4, 0, byArray4.length);
            byte[] byArray5 = new byte[n2 + this.macLength];
            for (int i = 0; i != n2; ++i) {
                byArray5[i] = (byte)(byArray[n + i] ^ byArray4[i]);
            }
            KeyParameter keyParameter = new KeyParameter(byArray4, n2, byArray4.length - n2);
            this.hMac.init(keyParameter);
            this.hMac.update(byArray5, 0, n2);
            byte[] byArray6 = new byte[this.hMac.getMacSize()];
            this.hMac.doFinal(byArray6, 0);
            Arrays.clear(keyParameter.getKey());
            Arrays.clear(byArray4);
            System.arraycopy(byArray6, 0, byArray5, n2, this.macLength);
            return Arrays.concatenate(ephemeralKeyPair.getEncodedPublicKey(), byArray5);
        }
        if (this.state == 2 || this.state == 4) {
            ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
            ECCurve eCCurve = eCPrivateKeyParameters.getParameters().getCurve();
            int n3 = (eCCurve.getFieldSize() + 7) / 8;
            n3 = byArray[n] == 4 ? 1 + 2 * n3 : 1 + n3;
            int n4 = n2 - (n3 + this.macLength);
            ECPoint eCPoint = eCCurve.decodePoint(Arrays.copyOfRange(byArray, n, n + n3));
            this.agreement.init(this.key);
            byte[] byArray7 = converter.integerToBytes(this.agreement.calculateAgreement(new ECPublicKeyParameters(eCPoint, eCPrivateKeyParameters.getParameters())), converter.getByteLength(eCDomainParameters.getCurve()));
            byte[] byArray8 = new byte[n4 + this.macKeyLength];
            this.kdf.init(new KDFParameters(byArray7, this.engineSpec.getRecipientInfo()));
            this.kdf.generateBytes(byArray8, 0, byArray8.length);
            byte[] byArray9 = new byte[n4];
            for (int i = 0; i != byArray9.length; ++i) {
                byArray9[i] = (byte)(byArray[n + n3 + i] ^ byArray8[i]);
            }
            KeyParameter keyParameter = new KeyParameter(byArray8, n4, byArray8.length - n4);
            this.hMac.init(keyParameter);
            this.hMac.update(byArray, n + n3, byArray9.length);
            byte[] byArray10 = new byte[this.hMac.getMacSize()];
            this.hMac.doFinal(byArray10, 0);
            Arrays.clear(keyParameter.getKey());
            Arrays.clear(byArray8);
            if (!Arrays.constantTimeAreEqual(this.macLength, byArray10, 0, byArray, n + (n2 - this.macLength))) {
                throw new BadPaddingException("mac field");
            }
            return byArray9;
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public int engineDoFinal(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] byArray3 = this.engineDoFinal(byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, byArray3.length);
        return byArray3.length;
    }

    public static class KEM
    extends IESKEMCipher {
        public KEM(Digest digest, Digest digest2, int n, int n2) {
            super(new ECDHCBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2), n, n2);
        }
    }

    public static class KEMwithSHA256
    extends KEM {
        public KEMwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256(), 32, 16);
        }
    }
}

