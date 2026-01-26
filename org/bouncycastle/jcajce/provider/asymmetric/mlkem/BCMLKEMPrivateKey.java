/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCMLKEMPrivateKey
implements MLKEMPrivateKey,
BCKey {
    private static final long serialVersionUID = 1L;
    private transient MLKEMPrivateKeyParameters params;
    private transient String algorithm;
    private transient ASN1Set attributes;
    private transient byte[] priorEncoding;

    public BCMLKEMPrivateKey(MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters) {
        this.params = mLKEMPrivateKeyParameters;
        this.algorithm = Strings.toUpperCase(mLKEMPrivateKeyParameters.getParameters().getName());
    }

    public BCMLKEMPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attributes = privateKeyInfo.getAttributes();
        this.priorEncoding = privateKeyInfo.getEncoded();
        this.params = (MLKEMPrivateKeyParameters)PrivateKeyFactory.createKey(privateKeyInfo);
        this.algorithm = Strings.toUpperCase(MLKEMParameterSpec.fromName(this.params.getParameters().getName()).getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCMLKEMPrivateKey) {
            BCMLKEMPrivateKey bCMLKEMPrivateKey = (BCMLKEMPrivateKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCMLKEMPrivateKey.params.getEncoded());
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
    public byte[] getEncoded() {
        try {
            if (this.priorEncoding != null) {
                return this.priorEncoding;
            }
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
            return privateKeyInfo.getEncoded();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    @Override
    public MLKEMPublicKey getPublicKey() {
        return new BCMLKEMPublicKey(this.params.getPublicKeyParameters());
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
    public MLKEMPrivateKey getPrivateKey(boolean bl) {
        byte[] byArray;
        if (bl && (byArray = this.params.getSeed()) != null) {
            return new BCMLKEMPrivateKey(this.params.getParametersWithFormat(1));
        }
        return new BCMLKEMPrivateKey(this.params.getParametersWithFormat(2));
    }

    @Override
    public MLKEMParameterSpec getParameterSpec() {
        return MLKEMParameterSpec.fromName(this.params.getParameters().getName());
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

    MLKEMPrivateKeyParameters getKeyParams() {
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

