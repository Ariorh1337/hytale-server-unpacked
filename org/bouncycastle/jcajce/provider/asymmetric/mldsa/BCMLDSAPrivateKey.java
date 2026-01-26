/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPublicKey;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCMLDSAPrivateKey
implements MLDSAPrivateKey,
BCKey {
    private static final long serialVersionUID = 1L;
    private transient MLDSAPrivateKeyParameters params;
    private transient String algorithm;
    private transient byte[] encoding;
    private transient ASN1Set attributes;

    public BCMLDSAPrivateKey(MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters) {
        this.params = mLDSAPrivateKeyParameters;
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(mLDSAPrivateKeyParameters.getParameters().getName()).getName());
    }

    public BCMLDSAPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.encoding = privateKeyInfo.getEncoded();
        this.init((MLDSAPrivateKeyParameters)PrivateKeyFactory.createKey(privateKeyInfo), privateKeyInfo.getAttributes());
    }

    private void init(MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters, ASN1Set aSN1Set) {
        this.attributes = aSN1Set;
        this.params = mLDSAPrivateKeyParameters;
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(mLDSAPrivateKeyParameters.getParameters().getName()).getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCMLDSAPrivateKey) {
            BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCMLDSAPrivateKey.params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override
    public final String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public MLDSAPrivateKey getPrivateKey(boolean bl) {
        byte[] byArray;
        if (bl && (byArray = this.params.getSeed()) != null) {
            return new BCMLDSAPrivateKey(this.params.getParametersWithFormat(1));
        }
        return new BCMLDSAPrivateKey(this.params.getParametersWithFormat(2));
    }

    @Override
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = KeyUtil.getEncodedPrivateKeyInfo(this.params, this.attributes);
        }
        return Arrays.clone(this.encoding);
    }

    @Override
    public MLDSAPublicKey getPublicKey() {
        MLDSAPublicKeyParameters mLDSAPublicKeyParameters = this.params.getPublicKeyParameters();
        if (mLDSAPublicKeyParameters == null) {
            return null;
        }
        return new BCMLDSAPublicKey(mLDSAPublicKeyParameters);
    }

    @Override
    public byte[] getPrivateData() {
        return this.params.getEncoded();
    }

    @Override
    public byte[] getSeed() {
        return this.params.getSeed();
    }

    @Override
    public MLDSAParameterSpec getParameterSpec() {
        return MLDSAParameterSpec.fromName(this.params.getParameters().getName());
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        byte[] byArray = this.params.getPublicKey();
        stringBuilder.append(this.getAlgorithm()).append(" ").append("Private Key").append(" [").append(new Fingerprint(byArray).toString()).append("]").append(string).append("    public data: ").append(Hex.toHexString(byArray)).append(string);
        return stringBuilder.toString();
    }

    MLDSAPrivateKeyParameters getKeyParams() {
        return this.params;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        byte[] byArray = (byte[])objectInputStream.readObject();
        this.init(PrivateKeyInfo.getInstance(byArray));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}

