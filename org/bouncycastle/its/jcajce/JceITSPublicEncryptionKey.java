/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.its.jcajce.ECUtil;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class JceITSPublicEncryptionKey
extends ITSPublicEncryptionKey {
    private final JcaJceHelper helper;

    JceITSPublicEncryptionKey(PublicEncryptionKey publicEncryptionKey, JcaJceHelper jcaJceHelper) {
        super(publicEncryptionKey);
        this.helper = jcaJceHelper;
    }

    JceITSPublicEncryptionKey(PublicKey publicKey, JcaJceHelper jcaJceHelper) {
        super(JceITSPublicEncryptionKey.fromPublicKey(publicKey));
        this.helper = jcaJceHelper;
    }

    static PublicEncryptionKey fromPublicKey(PublicKey publicKey) {
        if (!(publicKey instanceof ECPublicKey)) {
            throw new IllegalArgumentException("must be ECPublicKey instance");
        }
        ECPublicKey eCPublicKey = (ECPublicKey)publicKey;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm().getParameters());
        if (aSN1ObjectIdentifier.equals(SECObjectIdentifiers.secp256r1)) {
            return new PublicEncryptionKey(SymmAlgorithm.aes128Ccm, new BasePublicEncryptionKey.Builder().setChoice(0).setValue(EccP256CurvePoint.uncompressedP256(eCPublicKey.getW().getAffineX(), eCPublicKey.getW().getAffineY())).createBasePublicEncryptionKey());
        }
        if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicEncryptionKey(SymmAlgorithm.aes128Ccm, new BasePublicEncryptionKey.Builder().setChoice(1).setValue(EccP256CurvePoint.uncompressedP256(eCPublicKey.getW().getAffineX(), eCPublicKey.getW().getAffineY())).createBasePublicEncryptionKey());
        }
        throw new IllegalArgumentException("unknown curve in public encryption key");
    }

    public PublicKey getKey() {
        byte[] byArray;
        X9ECParameters x9ECParameters;
        BasePublicEncryptionKey basePublicEncryptionKey = this.encryptionKey.getPublicKey();
        switch (basePublicEncryptionKey.getChoice()) {
            case 0: {
                x9ECParameters = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
                break;
            }
            case 1: {
                x9ECParameters = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        ASN1Encodable aSN1Encodable = this.encryptionKey.getPublicKey().getBasePublicEncryptionKey();
        if (!(aSN1Encodable instanceof EccCurvePoint)) {
            throw new IllegalStateException("extension to public verification key not supported");
        }
        EccCurvePoint eccCurvePoint = (EccCurvePoint)basePublicEncryptionKey.getBasePublicEncryptionKey();
        ECCurve eCCurve = x9ECParameters.getCurve();
        if (eccCurvePoint instanceof EccP256CurvePoint) {
            byArray = eccCurvePoint.getEncodedPoint();
        } else if (eccCurvePoint instanceof EccP384CurvePoint) {
            byArray = eccCurvePoint.getEncodedPoint();
        } else {
            throw new IllegalStateException("unknown key type");
        }
        org.bouncycastle.math.ec.ECPoint eCPoint = eCCurve.decodePoint(byArray).normalize();
        try {
            KeyFactory keyFactory = this.helper.createKeyFactory("EC");
            ECParameterSpec eCParameterSpec = ECUtil.convertToSpec(x9ECParameters);
            ECPoint eCPoint2 = ECUtil.convertPoint(eCPoint);
            return keyFactory.generatePublic(new ECPublicKeySpec(eCPoint2, eCParameterSpec));
        }
        catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    public static class Builder {
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String string) {
            this.helper = new NamedJcaJceHelper(string);
            return this;
        }

        public JceITSPublicEncryptionKey build(PublicEncryptionKey publicEncryptionKey) {
            return new JceITSPublicEncryptionKey(publicEncryptionKey, this.helper);
        }

        public JceITSPublicEncryptionKey build(PublicKey publicKey) {
            return new JceITSPublicEncryptionKey(publicKey, this.helper);
        }
    }
}

