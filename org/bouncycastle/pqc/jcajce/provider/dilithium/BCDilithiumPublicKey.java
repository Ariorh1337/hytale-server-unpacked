/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCDilithiumPublicKey
implements DilithiumPublicKey {
    private static final long serialVersionUID = 1L;
    private transient DilithiumPublicKeyParameters params;
    private transient String algorithm;
    private transient byte[] encoding;

    public BCDilithiumPublicKey(DilithiumPublicKeyParameters dilithiumPublicKeyParameters) {
        this.init(dilithiumPublicKeyParameters);
    }

    public BCDilithiumPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.init((DilithiumPublicKeyParameters)PublicKeyFactory.createKey(subjectPublicKeyInfo));
    }

    private void init(DilithiumPublicKeyParameters dilithiumPublicKeyParameters) {
        this.params = dilithiumPublicKeyParameters;
        this.algorithm = Strings.toUpperCase(dilithiumPublicKeyParameters.getParameters().getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCDilithiumPublicKey) {
            BCDilithiumPublicKey bCDilithiumPublicKey = (BCDilithiumPublicKey)object;
            return Arrays.areEqual(this.getEncoded(), bCDilithiumPublicKey.getEncoded());
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
    public DilithiumParameterSpec getParameterSpec() {
        return DilithiumParameterSpec.fromName(this.params.getParameters().getName());
    }

    DilithiumPublicKeyParameters getKeyParams() {
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

