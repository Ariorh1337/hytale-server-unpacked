/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class ASN1Integer
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Integer.class, 2){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1Integer.createPrimitive(dEROctetString.getOctets());
        }
    };
    static final int SIGN_EXT_SIGNED = -1;
    static final int SIGN_EXT_UNSIGNED = 255;
    private final byte[] bytes;
    private final int start;

    public static ASN1Integer getInstance(Object object) {
        if (object == null || object instanceof ASN1Integer) {
            return (ASN1Integer)object;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1Integer)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1Integer getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Integer)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1Integer getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Integer)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public ASN1Integer(long l) {
        this.bytes = BigInteger.valueOf(l).toByteArray();
        this.start = 0;
    }

    public ASN1Integer(BigInteger bigInteger) {
        this.bytes = bigInteger.toByteArray();
        this.start = 0;
    }

    public ASN1Integer(byte[] byArray) {
        this(byArray, true);
    }

    ASN1Integer(byte[] byArray, boolean bl) {
        if (ASN1Integer.isMalformed(byArray)) {
            throw new IllegalArgumentException("malformed integer");
        }
        this.bytes = bl ? Arrays.clone(byArray) : byArray;
        this.start = ASN1Integer.signBytesToSkip(byArray);
    }

    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    public boolean hasValue(int n) {
        return this.bytes.length - this.start <= 4 && ASN1Integer.intValue(this.bytes, this.start, -1) == n;
    }

    public boolean hasValue(long l) {
        return this.bytes.length - this.start <= 8 && ASN1Integer.longValue(this.bytes, this.start, -1) == l;
    }

    public boolean hasValue(BigInteger bigInteger) {
        return null != bigInteger && ASN1Integer.intValue(this.bytes, this.start, -1) == bigInteger.intValue() && this.getValue().equals(bigInteger);
    }

    public int intPositiveValueExact() {
        int n = this.bytes.length - this.start;
        if (n > 4 || n == 4 && 0 != (this.bytes[this.start] & 0x80)) {
            throw new ArithmeticException("ASN.1 Integer out of positive int range");
        }
        return ASN1Integer.intValue(this.bytes, this.start, 255);
    }

    public int intValueExact() {
        int n = this.bytes.length - this.start;
        if (n > 4) {
            throw new ArithmeticException("ASN.1 Integer out of int range");
        }
        return ASN1Integer.intValue(this.bytes, this.start, -1);
    }

    public long longValueExact() {
        int n = this.bytes.length - this.start;
        if (n > 8) {
            throw new ArithmeticException("ASN.1 Integer out of long range");
        }
        return ASN1Integer.longValue(this.bytes, this.start, -1);
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.bytes.length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 2, this.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Integer)) {
            return false;
        }
        ASN1Integer aSN1Integer = (ASN1Integer)aSN1Primitive;
        return Arrays.areEqual(this.bytes, aSN1Integer.bytes);
    }

    public String toString() {
        return this.getValue().toString();
    }

    static ASN1Integer createPrimitive(byte[] byArray) {
        return new ASN1Integer(byArray, false);
    }

    static int intValue(byte[] byArray, int n, int n2) {
        int n3 = byArray.length;
        int n4 = Math.max(n, n3 - 4);
        int n5 = byArray[n4] & n2;
        while (++n4 < n3) {
            n5 = n5 << 8 | byArray[n4] & 0xFF;
        }
        return n5;
    }

    static long longValue(byte[] byArray, int n, int n2) {
        int n3 = byArray.length;
        int n4 = Math.max(n, n3 - 8);
        long l = byArray[n4] & n2;
        while (++n4 < n3) {
            l = l << 8 | (long)(byArray[n4] & 0xFF);
        }
        return l;
    }

    static boolean isMalformed(byte[] byArray) {
        switch (byArray.length) {
            case 0: {
                return true;
            }
            case 1: {
                return false;
            }
        }
        return byArray[0] == byArray[1] >> 7 && !Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer");
    }

    static int signBytesToSkip(byte[] byArray) {
        int n;
        int n2 = byArray.length - 1;
        for (n = 0; n < n2 && byArray[n] == byArray[n + 1] >> 7; ++n) {
        }
        return n;
    }
}

