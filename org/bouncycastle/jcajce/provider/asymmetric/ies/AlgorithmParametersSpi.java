/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ies;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    IESParameterSpec currentSpec;

    protected boolean isASN1FormatString(String string) {
        return string == null || string.equals("ASN.1");
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class clazz) throws InvalidParameterSpecException {
        if (clazz == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(clazz);
    }

    @Override
    protected byte[] engineGetEncoded() {
        try {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            if (this.currentSpec.getDerivationV() != null) {
                aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(this.currentSpec.getDerivationV())));
            }
            if (this.currentSpec.getEncodingV() != null) {
                aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable)new DEROctetString(this.currentSpec.getEncodingV())));
            }
            aSN1EncodableVector.add(new ASN1Integer(this.currentSpec.getMacKeySize()));
            byte[] byArray = this.currentSpec.getNonce();
            if (byArray != null) {
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                aSN1EncodableVector2.add(new ASN1Integer(this.currentSpec.getCipherKeySize()));
                aSN1EncodableVector2.add(new DEROctetString(byArray));
                aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
            }
            aSN1EncodableVector.add(this.currentSpec.getPointCompression() ? ASN1Boolean.TRUE : ASN1Boolean.FALSE);
            return new DERSequence(aSN1EncodableVector).getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new RuntimeException("Error encoding IESParameters");
        }
    }

    @Override
    protected byte[] engineGetEncoded(String string) {
        if (this.isASN1FormatString(string) || string.equalsIgnoreCase("X.509")) {
            return this.engineGetEncoded();
        }
        return null;
    }

    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class clazz) throws InvalidParameterSpecException {
        if (clazz == IESParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof IESParameterSpec)) {
            throw new InvalidParameterSpecException("IESParameterSpec required to initialise a IES algorithm parameters object");
        }
        this.currentSpec = (IESParameterSpec)algorithmParameterSpec;
    }

    @Override
    protected void engineInit(byte[] byArray) throws IOException {
        try {
            ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(byArray);
            if (aSN1Sequence.size() > 5) {
                throw new IOException("sequence too big");
            }
            byte[] byArray2 = null;
            byte[] byArray3 = null;
            BigInteger bigInteger = null;
            BigInteger bigInteger2 = null;
            byte[] byArray4 = null;
            boolean bl = false;
            Enumeration enumeration = aSN1Sequence.getObjects();
            while (enumeration.hasMoreElements()) {
                ASN1Primitive aSN1Primitive;
                Object e = enumeration.nextElement();
                if (e instanceof ASN1TaggedObject) {
                    aSN1Primitive = ASN1TaggedObject.getInstance(e);
                    if (((ASN1TaggedObject)aSN1Primitive).getTagNo() == 0) {
                        byArray2 = ASN1OctetString.getInstance((ASN1TaggedObject)aSN1Primitive, false).getOctets();
                        continue;
                    }
                    if (((ASN1TaggedObject)aSN1Primitive).getTagNo() != 1) continue;
                    byArray3 = ASN1OctetString.getInstance((ASN1TaggedObject)aSN1Primitive, false).getOctets();
                    continue;
                }
                if (e instanceof ASN1Integer) {
                    bigInteger = ASN1Integer.getInstance(e).getValue();
                    continue;
                }
                if (e instanceof ASN1Sequence) {
                    aSN1Primitive = ASN1Sequence.getInstance(e);
                    bigInteger2 = ASN1Integer.getInstance(((ASN1Sequence)aSN1Primitive).getObjectAt(0)).getValue();
                    byArray4 = ASN1OctetString.getInstance(((ASN1Sequence)aSN1Primitive).getObjectAt(1)).getOctets();
                    continue;
                }
                if (!(e instanceof ASN1Boolean)) continue;
                bl = ASN1Boolean.getInstance(e).isTrue();
            }
            this.currentSpec = bigInteger2 != null ? new IESParameterSpec(byArray2, byArray3, bigInteger.intValue(), bigInteger2.intValue(), byArray4, bl) : new IESParameterSpec(byArray2, byArray3, bigInteger.intValue(), -1, null, bl);
        }
        catch (ClassCastException classCastException) {
            throw new IOException("Not a valid IES Parameter encoding.");
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IOException("Not a valid IES Parameter encoding.");
        }
    }

    @Override
    protected void engineInit(byte[] byArray, String string) throws IOException {
        if (!this.isASN1FormatString(string) && !string.equalsIgnoreCase("X.509")) {
            throw new IOException("Unknown parameter format " + string);
        }
        this.engineInit(byArray);
    }

    @Override
    protected String engineToString() {
        return "IES Parameters";
    }
}

