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
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPrivateKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCEdDSAPrivateKey
implements EdDSAPrivateKey,
BCKey {
    static final long serialVersionUID = 1L;
    transient AsymmetricKeyParameter eddsaPrivateKey;
    transient AsymmetricKeyParameter eddsaPublicKey;
    transient int hashCode;
    private final boolean hasPublicKey;
    private final byte[] attributes;

    BCEdDSAPrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.hasPublicKey = true;
        this.attributes = null;
        this.eddsaPrivateKey = asymmetricKeyParameter;
        this.eddsaPublicKey = this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters ? ((Ed448PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey() : ((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
        this.hashCode = this.calculateHashCode();
    }

    BCEdDSAPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.hasPublicKey = privateKeyInfo.hasPublicKey();
        this.attributes = privateKeyInfo.getAttributes() != null ? privateKeyInfo.getAttributes().getEncoded() : null;
        this.populateFromPrivateKeyInfo(privateKeyInfo);
    }

    private void populateFromPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) throws IOException {
        byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
        if (EdECObjectIdentifiers.id_Ed448.equals(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
            this.eddsaPrivateKey = new Ed448PrivateKeyParameters(byArray);
            this.eddsaPublicKey = ((Ed448PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
        } else {
            this.eddsaPrivateKey = new Ed25519PrivateKeyParameters(byArray);
            this.eddsaPublicKey = ((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
        }
        this.hashCode = this.calculateHashCode();
    }

    @Override
    public String getAlgorithm() {
        if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
            return "EdDSA";
        }
        return this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters ? "Ed448" : "Ed25519";
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
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.eddsaPrivateKey, aSN1Set);
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
    public EdDSAPublicKey getPublicKey() {
        return new BCEdDSAPublicKey(this.eddsaPublicKey);
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.eddsaPrivateKey;
    }

    public String toString() {
        return Utils.keyToString("Private Key", this.getAlgorithm(), this.eddsaPublicKey);
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
        PrivateKeyInfo privateKeyInfo3 = privateKeyInfo = privateKey instanceof BCEdDSAPrivateKey ? ((BCEdDSAPrivateKey)privateKey).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(privateKey.getEncoded());
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
        byte[] byArray = this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? ((Ed448PublicKeyParameters)this.eddsaPublicKey).getEncoded() : ((Ed25519PublicKeyParameters)this.eddsaPublicKey).getEncoded();
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

