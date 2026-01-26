/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECCurve;

public class ECKeyUtil {
    public static ECPublicKey createKeyWithCompression(ECPublicKey eCPublicKey) {
        return new ECPublicKeyWithCompression(eCPublicKey);
    }

    private static class ECPublicKeyWithCompression
    implements ECPublicKey {
        private final ECPublicKey ecPublicKey;

        public ECPublicKeyWithCompression(ECPublicKey eCPublicKey) {
            this.ecPublicKey = eCPublicKey;
        }

        @Override
        public ECPoint getW() {
            return this.ecPublicKey.getW();
        }

        @Override
        public String getAlgorithm() {
            return this.ecPublicKey.getAlgorithm();
        }

        @Override
        public String getFormat() {
            return this.ecPublicKey.getFormat();
        }

        @Override
        public byte[] getEncoded() {
            ECCurve eCCurve;
            Object object;
            Object object2;
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(this.ecPublicKey.getEncoded());
            X962Parameters x962Parameters = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            if (x962Parameters.isNamedCurve()) {
                object2 = (ASN1ObjectIdentifier)x962Parameters.getParameters();
                object = CustomNamedCurves.getByOIDLazy((ASN1ObjectIdentifier)object2);
                if (object == null) {
                    object = ECNamedCurveTable.getByOIDLazy((ASN1ObjectIdentifier)object2);
                }
                eCCurve = ((X9ECParametersHolder)object).getCurve();
            } else {
                if (x962Parameters.isImplicitlyCA()) {
                    throw new IllegalStateException("unable to identify implictlyCA");
                }
                object2 = X9ECParameters.getInstance(x962Parameters.getParameters());
                eCCurve = ((X9ECParameters)object2).getCurve();
            }
            object2 = eCCurve.decodePoint(subjectPublicKeyInfo.getPublicKeyData().getOctets());
            object = ASN1OctetString.getInstance(new X9ECPoint((org.bouncycastle.math.ec.ECPoint)object2, true).toASN1Primitive());
            try {
                return new SubjectPublicKeyInfo(subjectPublicKeyInfo.getAlgorithm(), ((ASN1OctetString)object).getOctets()).getEncoded();
            }
            catch (IOException iOException) {
                throw new IllegalStateException("unable to encode EC public key: " + iOException.getMessage());
            }
        }

        @Override
        public ECParameterSpec getParams() {
            return this.ecPublicKey.getParams();
        }
    }
}

