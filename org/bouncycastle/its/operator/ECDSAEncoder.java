/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.operator;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.util.BigIntegers;

public class ECDSAEncoder {
    public static byte[] toX962(Signature signature) {
        byte[] byArray;
        byte[] byArray2;
        if (signature.getChoice() == 0 || signature.getChoice() == 1) {
            EcdsaP256Signature ecdsaP256Signature = EcdsaP256Signature.getInstance(signature.getSignature());
            byArray2 = ASN1OctetString.getInstance(ecdsaP256Signature.getRSig().getEccp256CurvePoint()).getOctets();
            byArray = ecdsaP256Signature.getSSig().getOctets();
        } else {
            EcdsaP384Signature ecdsaP384Signature = EcdsaP384Signature.getInstance(signature.getSignature());
            byArray2 = ASN1OctetString.getInstance(ecdsaP384Signature.getRSig().getEccP384CurvePoint()).getOctets();
            byArray = ecdsaP384Signature.getSSig().getOctets();
        }
        try {
            return new DERSequence(new ASN1Encodable[]{new ASN1Integer(BigIntegers.fromUnsignedByteArray(byArray2)), new ASN1Integer(BigIntegers.fromUnsignedByteArray(byArray))}).getEncoded();
        }
        catch (IOException iOException) {
            throw new RuntimeException("der encoding r & s");
        }
    }

    public static Signature toITS(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] byArray) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(byArray);
        if (aSN1ObjectIdentifier.equals(SECObjectIdentifiers.secp256r1)) {
            return new Signature(0, new EcdsaP256Signature(new EccP256CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue()))), new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue()))));
        }
        if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new Signature(1, new EcdsaP256Signature(new EccP256CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue()))), new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue()))));
        }
        if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            return new Signature(2, new EcdsaP384Signature(new EccP384CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(48, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue()))), new DEROctetString(BigIntegers.asUnsignedByteArray(48, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue()))));
        }
        throw new IllegalArgumentException("unknown curveID");
    }
}

