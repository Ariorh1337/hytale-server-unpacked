/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class BCECPrivateKey
implements java.security.interfaces.ECPrivateKey,
ECPrivateKey,
PKCS12BagAttributeCarrier,
ECPointEncoder,
BCKey {
    static final long serialVersionUID = 994553197664784084L;
    private String algorithm = "EC";
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    private transient ASN1BitString publicKey;
    private transient PrivateKeyInfo privateKeyInfo;
    private transient byte[] encoding;
    private transient ECPrivateKeyParameters baseKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCECPrivateKey() {
    }

    public BCECPrivateKey(java.security.interfaces.ECPrivateKey eCPrivateKey, ProviderConfiguration providerConfiguration) {
        this.d = eCPrivateKey.getS();
        this.algorithm = eCPrivateKey.getAlgorithm();
        this.ecSpec = eCPrivateKey.getParams();
        this.configuration = providerConfiguration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String string, org.bouncycastle.jce.spec.ECPrivateKeySpec eCPrivateKeySpec, ProviderConfiguration providerConfiguration) {
        this.algorithm = string;
        this.d = eCPrivateKeySpec.getD();
        if (eCPrivateKeySpec.getParams() != null) {
            ECCurve eCCurve = eCPrivateKeySpec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCPrivateKeySpec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, eCPrivateKeySpec.getParams());
        } else {
            this.ecSpec = null;
        }
        this.configuration = providerConfiguration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String string, ECPrivateKeySpec eCPrivateKeySpec, ProviderConfiguration providerConfiguration) {
        this.algorithm = string;
        this.d = eCPrivateKeySpec.getS();
        this.ecSpec = eCPrivateKeySpec.getParams();
        this.configuration = providerConfiguration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String string, BCECPrivateKey bCECPrivateKey) {
        this.algorithm = string;
        this.d = bCECPrivateKey.d;
        this.ecSpec = bCECPrivateKey.ecSpec;
        this.withCompression = bCECPrivateKey.withCompression;
        this.attrCarrier = bCECPrivateKey.attrCarrier;
        this.publicKey = bCECPrivateKey.publicKey;
        this.configuration = bCECPrivateKey.configuration;
        this.baseKey = bCECPrivateKey.baseKey;
    }

    public BCECPrivateKey(String string, ECPrivateKeyParameters eCPrivateKeyParameters, BCECPublicKey bCECPublicKey, ECParameterSpec eCParameterSpec, ProviderConfiguration providerConfiguration) {
        this.algorithm = string;
        this.d = eCPrivateKeyParameters.getD();
        this.configuration = providerConfiguration;
        this.baseKey = eCPrivateKeyParameters;
        if (eCParameterSpec == null) {
            ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(eCDomainParameters.getG()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
        } else {
            this.ecSpec = eCParameterSpec;
        }
        this.publicKey = this.getPublicKeyDetails(bCECPublicKey);
    }

    public BCECPrivateKey(String string, ECPrivateKeyParameters eCPrivateKeyParameters, BCECPublicKey bCECPublicKey, org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec, ProviderConfiguration providerConfiguration) {
        this.algorithm = string;
        this.d = eCPrivateKeyParameters.getD();
        this.configuration = providerConfiguration;
        this.baseKey = eCPrivateKeyParameters;
        if (eCParameterSpec == null) {
            ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(eCDomainParameters.getG()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(eCParameterSpec.getCurve(), eCParameterSpec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, eCParameterSpec);
        }
        try {
            this.publicKey = this.getPublicKeyDetails(bCECPublicKey);
        }
        catch (Exception exception) {
            this.publicKey = null;
        }
    }

    public BCECPrivateKey(String string, ECPrivateKeyParameters eCPrivateKeyParameters, ProviderConfiguration providerConfiguration) {
        this.algorithm = string;
        this.d = eCPrivateKeyParameters.getD();
        this.ecSpec = null;
        this.configuration = providerConfiguration;
        this.baseKey = eCPrivateKeyParameters;
    }

    BCECPrivateKey(String string, PrivateKeyInfo privateKeyInfo, ProviderConfiguration providerConfiguration) throws IOException {
        this.algorithm = string;
        this.configuration = providerConfiguration;
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo privateKeyInfo) throws IOException {
        X962Parameters x962Parameters = X962Parameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        ECCurve eCCurve = EC5Util.getCurve(this.configuration, x962Parameters);
        this.ecSpec = EC5Util.convertToSpec(x962Parameters, eCCurve);
        ASN1Encodable aSN1Encodable = privateKeyInfo.parsePrivateKey();
        if (aSN1Encodable instanceof ASN1Integer) {
            ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Encodable);
            this.d = aSN1Integer.getValue();
        } else {
            org.bouncycastle.asn1.sec.ECPrivateKey eCPrivateKey = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(aSN1Encodable);
            this.d = eCPrivateKey.getKey();
            this.publicKey = eCPrivateKey.getPublicKey();
        }
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        if (this.encoding == null) {
            PrivateKeyInfo privateKeyInfo = this.getPrivateKeyInfo();
            if (privateKeyInfo == null) {
                return null;
            }
            try {
                this.encoding = privateKeyInfo.getEncoded("DER");
            }
            catch (IOException iOException) {
                return null;
            }
        }
        return Arrays.clone(this.encoding);
    }

    private PrivateKeyInfo getPrivateKeyInfo() {
        if (this.privateKeyInfo == null) {
            X962Parameters x962Parameters = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
            int n = this.ecSpec == null ? ECUtil.getOrderBitLength(this.configuration, null, this.getS()) : ECUtil.getOrderBitLength(this.configuration, this.ecSpec.getOrder(), this.getS());
            org.bouncycastle.asn1.sec.ECPrivateKey eCPrivateKey = this.publicKey != null ? new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), this.publicKey, x962Parameters) : new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), (ASN1Encodable)x962Parameters);
            try {
                this.privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962Parameters), eCPrivateKey);
            }
            catch (IOException iOException) {
                return null;
            }
        }
        return this.privateKeyInfo;
    }

    public ECPrivateKeyParameters engineGetKeyParameters() {
        return this.baseKey;
    }

    @Override
    public ECParameterSpec getParams() {
        return this.ecSpec;
    }

    @Override
    public org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
        if (this.ecSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(this.ecSpec);
    }

    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec);
        }
        return this.configuration.getEcImplicitlyCa();
    }

    @Override
    public BigInteger getS() {
        return this.d;
    }

    @Override
    public BigInteger getD() {
        return this.d;
    }

    @Override
    public void setBagAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.attrCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Encodable);
    }

    @Override
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.attrCarrier.getBagAttribute(aSN1ObjectIdentifier);
    }

    @Override
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    @Override
    public boolean hasFriendlyName() {
        return this.attrCarrier.hasFriendlyName();
    }

    @Override
    public void setFriendlyName(String string) {
        this.attrCarrier.setFriendlyName(string);
    }

    @Override
    public void setPointFormat(String string) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(string);
    }

    public boolean equals(Object object) {
        if (object instanceof java.security.interfaces.ECPrivateKey) {
            PrivateKeyInfo privateKeyInfo;
            java.security.interfaces.ECPrivateKey eCPrivateKey = (java.security.interfaces.ECPrivateKey)object;
            PrivateKeyInfo privateKeyInfo2 = this.getPrivateKeyInfo();
            PrivateKeyInfo privateKeyInfo3 = privateKeyInfo = eCPrivateKey instanceof BCECPrivateKey ? ((BCECPrivateKey)eCPrivateKey).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(eCPrivateKey.getEncoded());
            if (privateKeyInfo2 == null || privateKeyInfo == null) {
                return false;
            }
            try {
                boolean bl = Arrays.constantTimeAreEqual(privateKeyInfo2.getPrivateKeyAlgorithm().getEncoded(), privateKeyInfo.getPrivateKeyAlgorithm().getEncoded());
                boolean bl2 = Arrays.constantTimeAreEqual(this.getS().toByteArray(), eCPrivateKey.getS().toByteArray());
                return bl & bl2;
            }
            catch (IOException iOException) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString("EC", this.d, this.engineGetSpec());
    }

    private ASN1BitString getPublicKeyDetails(BCECPublicKey bCECPublicKey) {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(bCECPublicKey.getEncoded()));
            return subjectPublicKeyInfo.getPublicKeyData();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        byte[] byArray = (byte[])objectInputStream.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(byArray)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }

    private static ECPrivateKeyParameters convertToBaseKey(BCECPrivateKey bCECPrivateKey) {
        String string;
        BCECPrivateKey bCECPrivateKey2 = bCECPrivateKey;
        org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = bCECPrivateKey2.getParameters();
        if (eCParameterSpec == null) {
            eCParameterSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
        }
        if (bCECPrivateKey2.getParameters() instanceof ECNamedCurveParameterSpec && (string = ((ECNamedCurveParameterSpec)bCECPrivateKey2.getParameters()).getName()) != null) {
            return new ECPrivateKeyParameters(bCECPrivateKey2.getD(), (ECDomainParameters)new ECNamedDomainParameters(ECNamedCurveTable.getOID(string), eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH(), eCParameterSpec.getSeed()));
        }
        return new ECPrivateKeyParameters(bCECPrivateKey2.getD(), new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH(), eCParameterSpec.getSeed()));
    }
}

