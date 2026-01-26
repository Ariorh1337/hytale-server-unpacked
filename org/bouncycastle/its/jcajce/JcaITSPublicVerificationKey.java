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
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.ECUtil;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class JcaITSPublicVerificationKey
extends ITSPublicVerificationKey {
    private final JcaJceHelper helper;

    JcaITSPublicVerificationKey(PublicVerificationKey publicVerificationKey, JcaJceHelper jcaJceHelper) {
        super(publicVerificationKey);
        this.helper = jcaJceHelper;
    }

    JcaITSPublicVerificationKey(PublicKey publicKey, JcaJceHelper jcaJceHelper) {
        super(JcaITSPublicVerificationKey.fromKeyParameters((ECPublicKey)publicKey));
        this.helper = jcaJceHelper;
    }

    static PublicVerificationKey fromKeyParameters(ECPublicKey eCPublicKey) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(SubjectPublicKeyInfo.getInstance(eCPublicKey.getEncoded()).getAlgorithm().getParameters());
        if (aSN1ObjectIdentifier.equals(SECObjectIdentifiers.secp256r1)) {
            return new PublicVerificationKey(0, EccP256CurvePoint.uncompressedP256(Point256.builder().setX(eCPublicKey.getW().getAffineX()).setY(eCPublicKey.getW().getAffineY()).createPoint256()));
        }
        if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicVerificationKey(1, EccP256CurvePoint.uncompressedP256(Point256.builder().setX(eCPublicKey.getW().getAffineX()).setY(eCPublicKey.getW().getAffineY()).createPoint256()));
        }
        if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            return new PublicVerificationKey(2, EccP384CurvePoint.uncompressedP384(Point384.builder().setX(eCPublicKey.getW().getAffineX()).setY(eCPublicKey.getW().getAffineY()).createPoint384()));
        }
        throw new IllegalArgumentException("unknown curve in public encryption key");
    }

    public PublicKey getKey() {
        byte[] byArray;
        X9ECParameters x9ECParameters;
        switch (this.verificationKey.getChoice()) {
            case 0: {
                x9ECParameters = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
                break;
            }
            case 1: {
                x9ECParameters = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
                break;
            }
            case 2: {
                x9ECParameters = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP384r1);
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        ECCurve eCCurve = x9ECParameters.getCurve();
        ASN1Encodable aSN1Encodable = this.verificationKey.getPublicVerificationKey();
        if (!(aSN1Encodable instanceof EccCurvePoint)) {
            throw new IllegalStateException("extension to public verification key not supported");
        }
        EccCurvePoint eccCurvePoint = (EccCurvePoint)this.verificationKey.getPublicVerificationKey();
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

        public JcaITSPublicVerificationKey build(PublicVerificationKey publicVerificationKey) {
            return new JcaITSPublicVerificationKey(publicVerificationKey, this.helper);
        }

        public JcaITSPublicVerificationKey build(PublicKey publicKey) {
            return new JcaITSPublicVerificationKey(publicKey, this.helper);
        }
    }
}

