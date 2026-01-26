/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositeUtil;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;

public class CompositePublicKey
implements PublicKey {
    private final List<PublicKey> keys;
    private final List<Provider> providers;
    private final AlgorithmIdentifier algorithmIdentifier;

    public static Builder builder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return new Builder(new AlgorithmIdentifier(aSN1ObjectIdentifier));
    }

    public static Builder builder(String string) {
        return CompositePublicKey.builder(CompositeUtil.getOid(string));
    }

    public CompositePublicKey(PublicKey ... publicKeyArray) {
        this(MiscObjectIdentifiers.id_composite_key, publicKeyArray);
    }

    public CompositePublicKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, PublicKey ... publicKeyArray) {
        this(new AlgorithmIdentifier(aSN1ObjectIdentifier), publicKeyArray);
    }

    public CompositePublicKey(AlgorithmIdentifier algorithmIdentifier, PublicKey ... publicKeyArray) {
        this.algorithmIdentifier = algorithmIdentifier;
        if (publicKeyArray == null || publicKeyArray.length == 0) {
            throw new IllegalArgumentException("at least one public key must be provided for the composite public key");
        }
        ArrayList<PublicKey> arrayList = new ArrayList<PublicKey>(publicKeyArray.length);
        for (int i = 0; i < publicKeyArray.length; ++i) {
            arrayList.add(publicKeyArray[i]);
        }
        this.keys = Collections.unmodifiableList(arrayList);
        this.providers = null;
    }

    public CompositePublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        CompositePublicKey compositePublicKey = null;
        try {
            if (!CompositeIndex.isAlgorithmSupported(aSN1ObjectIdentifier)) {
                throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
            }
            KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
            compositePublicKey = (CompositePublicKey)keyFactorySpi.generatePublic(subjectPublicKeyInfo);
            if (compositePublicKey == null) {
                throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
            }
        }
        catch (IOException iOException) {
            throw new IllegalStateException(iOException.getMessage(), iOException);
        }
        this.keys = compositePublicKey.getPublicKeys();
        this.algorithmIdentifier = compositePublicKey.getAlgorithmIdentifier();
        this.providers = null;
    }

    private CompositePublicKey(AlgorithmIdentifier algorithmIdentifier, PublicKey[] publicKeyArray, Provider[] providerArray) {
        this.algorithmIdentifier = algorithmIdentifier;
        if (publicKeyArray.length != 2) {
            throw new IllegalArgumentException("two keys required for composite private key");
        }
        ArrayList<PublicKey> arrayList = new ArrayList<PublicKey>(publicKeyArray.length);
        if (providerArray == null) {
            for (int i = 0; i < publicKeyArray.length; ++i) {
                arrayList.add(publicKeyArray[i]);
            }
            this.providers = null;
        } else {
            ArrayList<Provider> arrayList2 = new ArrayList<Provider>(providerArray.length);
            for (int i = 0; i < publicKeyArray.length; ++i) {
                arrayList2.add(providerArray[i]);
                arrayList.add(publicKeyArray[i]);
            }
            this.providers = Collections.unmodifiableList(arrayList2);
        }
        this.keys = Collections.unmodifiableList(arrayList);
    }

    public List<PublicKey> getPublicKeys() {
        return this.keys;
    }

    public List<Provider> getProviders() {
        return this.providers;
    }

    @Override
    public String getAlgorithm() {
        return CompositeIndex.getAlgorithmName(this.algorithmIdentifier.getAlgorithm());
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        if (this.algorithmIdentifier.getAlgorithm().on(IANAObjectIdentifiers.id_alg)) {
            try {
                byte[] byArray = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(PublicKeyFactory.createKey(this.keys.get(0).getEncoded())).getPublicKeyData().getBytes();
                byte[] byArray2 = org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(org.bouncycastle.crypto.util.PublicKeyFactory.createKey(this.keys.get(1).getEncoded())).getPublicKeyData().getBytes();
                return new SubjectPublicKeyInfo(this.getAlgorithmIdentifier(), Arrays.concatenate(byArray, byArray2)).getEncoded();
            }
            catch (IOException iOException) {
                throw new IllegalStateException("unable to encode composite public key: " + iOException.getMessage());
            }
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < this.keys.size(); ++i) {
            if (this.algorithmIdentifier.getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
                aSN1EncodableVector.add(SubjectPublicKeyInfo.getInstance(this.keys.get(i).getEncoded()));
                continue;
            }
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(this.keys.get(i).getEncoded());
            aSN1EncodableVector.add(subjectPublicKeyInfo.getPublicKeyData());
        }
        try {
            return new SubjectPublicKeyInfo(this.algorithmIdentifier, new DERSequence(aSN1EncodableVector)).getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to encode composite public key: " + iOException.getMessage());
        }
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CompositePublicKey) {
            boolean bl = true;
            CompositePublicKey compositePublicKey = (CompositePublicKey)object;
            if (!compositePublicKey.getAlgorithmIdentifier().equals(this.algorithmIdentifier) || !this.keys.equals(compositePublicKey.keys)) {
                bl = false;
            }
            return bl;
        }
        return false;
    }

    public static class Builder {
        private final AlgorithmIdentifier algorithmIdentifier;
        private final PublicKey[] keys = new PublicKey[2];
        private final Provider[] providers = new Provider[2];
        private int count = 0;

        private Builder(AlgorithmIdentifier algorithmIdentifier) {
            this.algorithmIdentifier = algorithmIdentifier;
        }

        public Builder addPublicKey(PublicKey publicKey) {
            return this.addPublicKey(publicKey, (Provider)null);
        }

        public Builder addPublicKey(PublicKey publicKey, String string) {
            return this.addPublicKey(publicKey, Security.getProvider(string));
        }

        public Builder addPublicKey(PublicKey publicKey, Provider provider) {
            if (this.count == this.keys.length) {
                throw new IllegalStateException("only " + this.keys.length + " allowed in composite");
            }
            this.keys[this.count] = publicKey;
            this.providers[this.count++] = provider;
            return this;
        }

        public CompositePublicKey build() {
            if (this.providers[0] == null && this.providers[1] == null) {
                return new CompositePublicKey(this.algorithmIdentifier, this.keys, null);
            }
            return new CompositePublicKey(this.algorithmIdentifier, this.keys, this.providers);
        }
    }
}

