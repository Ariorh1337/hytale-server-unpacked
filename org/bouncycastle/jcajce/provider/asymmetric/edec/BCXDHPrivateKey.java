/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPrivateKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCXDHPrivateKey
implements XDHPrivateKey {
    static final long serialVersionUID = 1L;
    transient AsymmetricKeyParameter xdhPrivateKey;
    transient AsymmetricKeyParameter xdhPublicKey;
    transient int hashCode;
    private final boolean hasPublicKey;
    private final byte[] attributes;

    BCXDHPrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.hasPublicKey = true;
        this.attributes = null;
        this.xdhPrivateKey = asymmetricKeyParameter;
        this.xdhPublicKey = this.xdhPrivateKey instanceof X448PrivateKeyParameters ? ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey() : ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        this.hashCode = this.calculateHashCode();
    }

    BCXDHPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.hasPublicKey = privateKeyInfo.hasPublicKey();
        this.attributes = privateKeyInfo.getAttributes() != null ? privateKeyInfo.getAttributes().getEncoded() : null;
        this.populateFromPrivateKeyInfo(privateKeyInfo);
    }

    private void populateFromPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) throws IOException {
        int n = privateKeyInfo.getPrivateKeyLength();
        byte[] byArray = n == 32 || n == 56 ? privateKeyInfo.getPrivateKey().getOctets() : ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
        if (EdECObjectIdentifiers.id_X448.equals(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
            this.xdhPrivateKey = new X448PrivateKeyParameters(byArray);
            this.xdhPublicKey = ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        } else {
            this.xdhPrivateKey = new X25519PrivateKeyParameters(byArray);
            this.xdhPublicKey = ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        }
        this.hashCode = this.calculateHashCode();
    }

    @Override
    public String getAlgorithm() {
        if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
            return "XDH";
        }
        return this.xdhPrivateKey instanceof X448PrivateKeyParameters ? "X448" : "X25519";
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        try {
            PrivateKeyInfo privateKeyInfo = this.getPrivateKeyInfo();
            if (privateKeyInfo == null) {
                return null;
            }
            return privateKeyInfo.getEncoded();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private PrivateKeyInfo getPrivateKeyInfo() {
        try {
            ASN1Set aSN1Set = ASN1Set.getInstance(this.attributes);
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.xdhPrivateKey, aSN1Set);
            if (this.hasPublicKey && !Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")) {
                return privateKeyInfo;
            }
            return new PrivateKeyInfo(privateKeyInfo.getPrivateKeyAlgorithm(), privateKeyInfo.parsePrivateKey(), aSN1Set);
        }
        catch (IOException iOException) {
            return null;
        }
    }

    @Override
    public XDHPublicKey getPublicKey() {
        return new BCXDHPublicKey(this.xdhPublicKey);
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPrivateKey;
    }

    public String toString() {
        return Utils.keyToString("Private Key", this.getAlgorithm(), this.xdhPublicKey);
    }

    public boolean equals(Object object) {
        PrivateKeyInfo privateKeyInfo;
        if (object == this) {
            return true;
        }
        if (!(object instanceof PrivateKey)) {
            return false;
        }
        PrivateKey privateKey = (PrivateKey)object;
        PrivateKeyInfo privateKeyInfo2 = this.getPrivateKeyInfo();
        PrivateKeyInfo privateKeyInfo3 = privateKeyInfo = privateKey instanceof BCXDHPrivateKey ? ((BCXDHPrivateKey)privateKey).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(privateKey.getEncoded());
        if (privateKeyInfo2 == null || privateKeyInfo == null) {
            return false;
        }
        try {
            boolean bl = Arrays.constantTimeAreEqual(privateKeyInfo2.getPrivateKeyAlgorithm().getEncoded(), privateKeyInfo.getPrivateKeyAlgorithm().getEncoded());
            boolean bl2 = Arrays.constantTimeAreEqual(privateKeyInfo2.getPrivateKey().getEncoded(), privateKeyInfo.getPrivateKey().getEncoded());
            return bl & bl2;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int calculateHashCode() {
        byte[] byArray = this.xdhPublicKey instanceof X448PublicKeyParameters ? ((X448PublicKeyParameters)this.xdhPublicKey).getEncoded() : ((X25519PublicKeyParameters)this.xdhPublicKey).getEncoded();
        int n = this.getAlgorithm().hashCode();
        n = 31 * n + Arrays.hashCode(byArray);
        return n;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        byte[] byArray = (byte[])objectInputStream.readObject();
        this.populateFromPrivateKeyInfo(PrivateKeyInfo.getInstance(byArray));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}

