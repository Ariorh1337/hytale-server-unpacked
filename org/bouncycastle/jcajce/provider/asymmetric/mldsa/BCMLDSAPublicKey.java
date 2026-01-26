/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCMLDSAPublicKey
implements MLDSAPublicKey,
BCKey {
    private static final long serialVersionUID = 1L;
    private transient MLDSAPublicKeyParameters params;
    private transient String algorithm;

    public BCMLDSAPublicKey(MLDSAPublicKeyParameters mLDSAPublicKeyParameters) {
        this.params = mLDSAPublicKeyParameters;
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(mLDSAPublicKeyParameters.getParameters().getName()).getName());
    }

    public BCMLDSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.params = (MLDSAPublicKeyParameters)PublicKeyFactory.createKey(subjectPublicKeyInfo);
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(this.params.getParameters().getName()).getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCMLDSAPublicKey) {
            BCMLDSAPublicKey bCMLDSAPublicKey = (BCMLDSAPublicKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCMLDSAPublicKey.params.getEncoded());
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
    public byte[] getPublicData() {
        return this.params.getEncoded();
    }

    @Override
    public byte[] getEncoded() {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params);
            return subjectPublicKeyInfo.getEncoded();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public MLDSAParameterSpec getParameterSpec() {
        return MLDSAParameterSpec.fromName(this.params.getParameters().getName());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        byte[] byArray = this.params.getEncoded();
        stringBuilder.append(this.getAlgorithm()).append(" ").append("Public Key").append(" [").append(new Fingerprint(byArray).toString()).append("]").append(string).append("    public data: ").append(Hex.toHexString(byArray)).append(string);
        return stringBuilder.toString();
    }

    MLDSAPublicKeyParameters getKeyParams() {
        return this.params;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        byte[] byArray = (byte[])objectInputStream.readObject();
        this.init(SubjectPublicKeyInfo.getInstance(byArray));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}

