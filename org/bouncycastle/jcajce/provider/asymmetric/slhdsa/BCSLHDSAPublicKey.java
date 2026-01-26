/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.SLHDSAPublicKey;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCSLHDSAPublicKey
implements SLHDSAPublicKey,
BCKey {
    private static final long serialVersionUID = 1L;
    private transient SLHDSAPublicKeyParameters params;

    public BCSLHDSAPublicKey(SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters) {
        this.params = sLHDSAPublicKeyParameters;
    }

    public BCSLHDSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.params = (SLHDSAPublicKeyParameters)PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCSLHDSAPublicKey) {
            BCSLHDSAPublicKey bCSLHDSAPublicKey = (BCSLHDSAPublicKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCSLHDSAPublicKey.params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override
    public final String getAlgorithm() {
        return "SLH-DSA-" + Strings.toUpperCase(this.params.getParameters().getName());
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
    public SLHDSAParameterSpec getParameterSpec() {
        return SLHDSAParameterSpec.fromName(this.params.getParameters().getName());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        byte[] byArray = this.params.getEncoded();
        stringBuilder.append(this.getAlgorithm()).append(" ").append("Public Key").append(" [").append(new Fingerprint(byArray).toString()).append("]").append(string).append("    public data: ").append(Hex.toHexString(byArray)).append(string);
        return stringBuilder.toString();
    }

    SLHDSAPublicKeyParameters getKeyParams() {
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

