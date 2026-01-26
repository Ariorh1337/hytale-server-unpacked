/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositeUtil;
import org.bouncycastle.jcajce.interfaces.MLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class CompositePrivateKey
implements PrivateKey {
    private final List<PrivateKey> keys;
    private final List<Provider> providers;
    private AlgorithmIdentifier algorithmIdentifier;

    public static Builder builder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return new Builder(new AlgorithmIdentifier(aSN1ObjectIdentifier));
    }

    public static Builder builder(String string) {
        return CompositePrivateKey.builder(CompositeUtil.getOid(string));
    }

    public CompositePrivateKey(PrivateKey ... privateKeyArray) {
        this(MiscObjectIdentifiers.id_composite_key, privateKeyArray);
    }

    public CompositePrivateKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, PrivateKey ... privateKeyArray) {
        this(new AlgorithmIdentifier(aSN1ObjectIdentifier), privateKeyArray);
    }

    public CompositePrivateKey(AlgorithmIdentifier algorithmIdentifier, PrivateKey ... privateKeyArray) {
        this.algorithmIdentifier = algorithmIdentifier;
        if (privateKeyArray == null || privateKeyArray.length == 0) {
            throw new IllegalArgumentException("at least one private key must be provided for the composite private key");
        }
        ArrayList<PrivateKey> arrayList = new ArrayList<PrivateKey>(privateKeyArray.length);
        for (int i = 0; i < privateKeyArray.length; ++i) {
            arrayList.add(this.processKey(privateKeyArray[i]));
        }
        this.keys = Collections.unmodifiableList(arrayList);
        this.providers = null;
    }

    private PrivateKey processKey(PrivateKey privateKey) {
        if (privateKey instanceof MLDSAPrivateKey) {
            try {
                return ((MLDSAPrivateKey)privateKey).getPrivateKey(true);
            }
            catch (Exception exception) {
                return privateKey;
            }
        }
        return privateKey;
    }

    private CompositePrivateKey(AlgorithmIdentifier algorithmIdentifier, PrivateKey[] privateKeyArray, Provider[] providerArray) {
        this.algorithmIdentifier = algorithmIdentifier;
        if (privateKeyArray.length != 2) {
            throw new IllegalArgumentException("two keys required for composite private key");
        }
        ArrayList<PrivateKey> arrayList = new ArrayList<PrivateKey>(privateKeyArray.length);
        if (providerArray == null) {
            for (int i = 0; i < privateKeyArray.length; ++i) {
                arrayList.add(this.processKey(privateKeyArray[i]));
            }
            this.providers = null;
        } else {
            ArrayList<Provider> arrayList2 = new ArrayList<Provider>(providerArray.length);
            for (int i = 0; i < privateKeyArray.length; ++i) {
                arrayList2.add(providerArray[i]);
                arrayList.add(this.processKey(privateKeyArray[i]));
            }
            this.providers = Collections.unmodifiableList(arrayList2);
        }
        this.keys = Collections.unmodifiableList(arrayList);
    }

    public CompositePrivateKey(PrivateKeyInfo privateKeyInfo) {
        CompositePrivateKey compositePrivateKey = null;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        try {
            if (!CompositeIndex.isAlgorithmSupported(aSN1ObjectIdentifier)) {
                throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
            }
            KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
            compositePrivateKey = (CompositePrivateKey)keyFactorySpi.generatePrivate(privateKeyInfo);
            if (compositePrivateKey == null) {
                throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
            }
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException(iOException.getMessage(), iOException);
        }
        this.keys = compositePrivateKey.getPrivateKeys();
        this.providers = null;
        this.algorithmIdentifier = compositePrivateKey.getAlgorithmIdentifier();
    }

    public List<PrivateKey> getPrivateKeys() {
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
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        if (this.algorithmIdentifier.getAlgorithm().on(IANAObjectIdentifiers.id_alg)) {
            try {
                byte[] byArray = ((MLDSAPrivateKey)this.keys.get(0)).getSeed();
                PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(PrivateKeyFactory.createKey(this.keys.get(1).getEncoded()));
                byte[] byArray2 = privateKeyInfo.getPrivateKey().getOctets();
                if (this.keys.get(1).getAlgorithm().contains("Ed")) {
                    byArray2 = ASN1OctetString.getInstance(byArray2).getOctets();
                } else if (this.keys.get(1).getAlgorithm().contains("EC")) {
                    ECPrivateKey eCPrivateKey = ECPrivateKey.getInstance(byArray2);
                    byArray2 = new ECPrivateKey(ECNamedCurveTable.getByOID(ASN1ObjectIdentifier.getInstance(eCPrivateKey.getParametersObject())).getCurve().getFieldSize(), eCPrivateKey.getKey(), (ASN1Encodable)eCPrivateKey.getParametersObject()).getEncoded();
                }
                return new PrivateKeyInfo(this.algorithmIdentifier, Arrays.concatenate(byArray, byArray2)).getEncoded();
            }
            catch (IOException iOException) {
                throw new IllegalStateException("unable to encode composite public key: " + iOException.getMessage());
            }
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.algorithmIdentifier.getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
            for (int i = 0; i < this.keys.size(); ++i) {
                PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(this.keys.get(i).getEncoded());
                aSN1EncodableVector.add(privateKeyInfo);
            }
            try {
                return new PrivateKeyInfo(this.algorithmIdentifier, new DERSequence(aSN1EncodableVector)).getEncoded("DER");
            }
            catch (IOException iOException) {
                throw new IllegalStateException("unable to encode composite private key: " + iOException.getMessage());
            }
        }
        byte[] byArray = null;
        for (int i = 0; i < this.keys.size(); ++i) {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(this.keys.get(i).getEncoded());
            byArray = Arrays.concatenate(byArray, privateKeyInfo.getPrivateKey().getOctets());
        }
        try {
            return new PrivateKeyInfo(this.algorithmIdentifier, byArray).getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to encode composite private key: " + iOException.getMessage());
        }
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CompositePrivateKey) {
            boolean bl = true;
            CompositePrivateKey compositePrivateKey = (CompositePrivateKey)object;
            if (!compositePrivateKey.getAlgorithmIdentifier().equals(this.algorithmIdentifier) || !this.keys.equals(compositePrivateKey.keys)) {
                bl = false;
            }
            return bl;
        }
        return false;
    }

    public static class Builder {
        private final AlgorithmIdentifier algorithmIdentifier;
        private final PrivateKey[] keys = new PrivateKey[2];
        private final Provider[] providers = new Provider[2];
        private int count = 0;

        private Builder(AlgorithmIdentifier algorithmIdentifier) {
            this.algorithmIdentifier = algorithmIdentifier;
        }

        public Builder addPrivateKey(PrivateKey privateKey) {
            return this.addPrivateKey(privateKey, (Provider)null);
        }

        public Builder addPrivateKey(PrivateKey privateKey, String string) {
            return this.addPrivateKey(privateKey, Security.getProvider(string));
        }

        public Builder addPrivateKey(PrivateKey privateKey, Provider provider) {
            if (this.count == this.keys.length) {
                throw new IllegalStateException("only " + this.keys.length + " allowed in composite");
            }
            this.keys[this.count] = privateKey;
            this.providers[this.count++] = provider;
            return this;
        }

        public CompositePrivateKey build() {
            if (this.providers[0] == null && this.providers[1] == null) {
                return new CompositePrivateKey(this.algorithmIdentifier, this.keys, null);
            }
            return new CompositePrivateKey(this.algorithmIdentifier, this.keys, this.providers);
        }
    }
}

