/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.cmce;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.CMCEKey;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCCMCEPrivateKey
implements PrivateKey,
CMCEKey {
    private static final long serialVersionUID = 1L;
    private transient CMCEPrivateKeyParameters params;
    private transient ASN1Set attributes;

    public BCCMCEPrivateKey(CMCEPrivateKeyParameters cMCEPrivateKeyParameters) {
        this.params = cMCEPrivateKeyParameters;
    }

    public BCCMCEPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attributes = privateKeyInfo.getAttributes();
        this.params = (CMCEPrivateKeyParameters)PrivateKeyFactory.createKey(privateKeyInfo);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BCCMCEPrivateKey) {
            BCCMCEPrivateKey bCCMCEPrivateKey = (BCCMCEPrivateKey)object;
            return Arrays.areEqual(this.params.getEncoded(), bCCMCEPrivateKey.params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override
    public final String getAlgorithm() {
        return Strings.toUpperCase(this.params.getParameters().getName());
    }

    @Override
    public byte[] getEncoded() {
        try {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
            return privateKeyInfo.getEncoded();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    @Override
    public CMCEParameterSpec getParameterSpec() {
        return CMCEParameterSpec.fromName(this.params.getParameters().getName());
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    CMCEPrivateKeyParameters getKeyParams() {
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

