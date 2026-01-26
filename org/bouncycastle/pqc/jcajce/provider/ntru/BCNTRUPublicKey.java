/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.NTRUKey;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.bouncycastle.util.Arrays;

public class BCNTRUPublicKey
implements PublicKey,
NTRUKey {
    private static final long serialVersionUID = 1L;
    private transient NTRUPublicKeyParameters params;

    public BCNTRUPublicKey(NTRUPublicKeyParameters nTRUPublicKeyParameters) {
        this.params = nTRUPublicKeyParameters;
    }

    public BCNTRUPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.params = (NTRUPublicKeyParameters)PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCNTRUPublicKey) {
            BCNTRUPublicKey bCNTRUPublicKey = (BCNTRUPublicKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCNTRUPublicKey.params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override
    public final String getAlgorithm() {
        return "NTRU";
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
    public NTRUParameterSpec getParameterSpec() {
        return NTRUParameterSpec.fromName(this.params.getParameters().getName());
    }

    NTRUPublicKeyParameters getKeyParams() {
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

