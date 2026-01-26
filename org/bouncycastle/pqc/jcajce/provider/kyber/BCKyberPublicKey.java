/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.kyber;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.KyberPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCKyberPublicKey
implements KyberPublicKey {
    private static final long serialVersionUID = 1L;
    private transient MLKEMPublicKeyParameters params;
    private transient String algorithm;
    private transient byte[] encoding;

    public BCKyberPublicKey(MLKEMPublicKeyParameters mLKEMPublicKeyParameters) {
        this.init(mLKEMPublicKeyParameters);
    }

    public BCKyberPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init((MLKEMPublicKeyParameters)PublicKeyFactory.createKey(subjectPublicKeyInfo));
    }

    private void init(MLKEMPublicKeyParameters mLKEMPublicKeyParameters) {
        this.params = mLKEMPublicKeyParameters;
        this.algorithm = Strings.toUpperCase(mLKEMPublicKeyParameters.getParameters().getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCKyberPublicKey) {
            BCKyberPublicKey bCKyberPublicKey = (BCKyberPublicKey)object;
            return Arrays.areEqual(this.getEncoded(), bCKyberPublicKey.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.getEncoded());
    }

    @Override
    public final String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = KeyUtil.getEncodedSubjectPublicKeyInfo(this.params);
        }
        return Arrays.clone(this.encoding);
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public KyberParameterSpec getParameterSpec() {
        return KyberParameterSpec.fromName(this.params.getParameters().getName());
    }

    MLKEMPublicKeyParameters getKeyParams() {
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

