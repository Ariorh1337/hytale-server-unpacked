/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class ASN1Enumerated
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Enumerated.class, 10){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1Enumerated.createPrimitive(dEROctetString.getOctets(), false);
        }
    };
    private final byte[] contents;
    private final int start;
    private static final ASN1Enumerated[] cache = new ASN1Enumerated[12];

    public static ASN1Enumerated getInstance(Object object) {
        if (object == null || object instanceof ASN1Enumerated) {
            return (ASN1Enumerated)object;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1Enumerated)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1Enumerated getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Enumerated)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1Enumerated getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Enumerated)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public ASN1Enumerated(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = BigInteger.valueOf(n).toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(BigInteger bigInteger) {
        if (bigInteger.signum() < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = bigInteger.toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(byte[] byArray) {
        this(byArray, true);
    }

    ASN1Enumerated(byte[] byArray, boolean bl) {
        if (ASN1Integer.isMalformed(byArray)) {
            throw new IllegalArgumentException("malformed enumerated");
        }
        if (0 != (byArray[0] & 0x80)) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = bl ? Arrays.clone(byArray) : byArray;
        this.start = ASN1Integer.signBytesToSkip(byArray);
    }

    public BigInteger getValue() {
        return new BigInteger(this.contents);
    }

    public boolean hasValue(int n) {
        return this.contents.length - this.start <= 4 && ASN1Integer.intValue(this.contents, this.start, -1) == n;
    }

    public boolean hasValue(BigInteger bigInteger) {
        return null != bigInteger && ASN1Integer.intValue(this.contents, this.start, -1) == bigInteger.intValue() && this.getValue().equals(bigInteger);
    }

    public int intValueExact() {
        int n = this.contents.length - this.start;
        if (n > 4) {
            throw new ArithmeticException("ASN.1 Enumerated out of int range");
        }
        return ASN1Integer.intValue(this.contents, this.start, -1);
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.contents.length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 10, this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Enumerated)) {
            return false;
        }
        ASN1Enumerated aSN1Enumerated = (ASN1Enumerated)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1Enumerated.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1Enumerated createPrimitive(byte[] byArray, boolean bl) {
        if (byArray.length > 1) {
            return new ASN1Enumerated(byArray, bl);
        }
        if (byArray.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int n = byArray[0] & 0xFF;
        if (n >= cache.length) {
            return new ASN1Enumerated(byArray, bl);
        }
        ASN1Enumerated aSN1Enumerated = cache[n];
        if (aSN1Enumerated == null) {
            aSN1Enumerated = ASN1Enumerated.cache[n] = new ASN1Enumerated(byArray, bl);
        }
        return aSN1Enumerated;
    }
}

