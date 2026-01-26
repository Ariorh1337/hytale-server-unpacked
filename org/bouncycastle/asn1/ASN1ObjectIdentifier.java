/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1RelativeOID;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.OIDTokenizer;
import org.bouncycastle.util.Arrays;

public class ASN1ObjectIdentifier
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectIdentifier.class, 6){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1ObjectIdentifier.createPrimitive(dEROctetString.getOctets(), false);
        }
    };
    private static final int MAX_CONTENTS_LENGTH = 4096;
    private static final int MAX_IDENTIFIER_LENGTH = 16385;
    private static final long LONG_LIMIT = 0xFFFFFFFFFFFF80L;
    private static final ConcurrentMap<OidHandle, ASN1ObjectIdentifier> pool = new ConcurrentHashMap<OidHandle, ASN1ObjectIdentifier>();
    private final byte[] contents;
    private String identifier;

    public static ASN1ObjectIdentifier fromContents(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("'contents' cannot be null");
        }
        return ASN1ObjectIdentifier.createPrimitive(byArray, true);
    }

    public static ASN1ObjectIdentifier getInstance(Object object) {
        if (object == null || object instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
                return (ASN1ObjectIdentifier)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1ObjectIdentifier)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive;
        if (!bl && !aSN1TaggedObject.isParsed() && aSN1TaggedObject.hasContextTag() && !((aSN1Primitive = aSN1TaggedObject.getBaseObject().toASN1Primitive()) instanceof ASN1ObjectIdentifier)) {
            return ASN1ObjectIdentifier.fromContents(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
        }
        return (ASN1ObjectIdentifier)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1ObjectIdentifier getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1ObjectIdentifier)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public static ASN1ObjectIdentifier tryFromID(String string) {
        byte[] byArray;
        if (string == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (string.length() <= 16385 && ASN1ObjectIdentifier.isValidIdentifier(string) && (byArray = ASN1ObjectIdentifier.parseIdentifier(string)).length <= 4096) {
            return new ASN1ObjectIdentifier(byArray, string);
        }
        return null;
    }

    public ASN1ObjectIdentifier(String string) {
        ASN1ObjectIdentifier.checkIdentifier(string);
        byte[] byArray = ASN1ObjectIdentifier.parseIdentifier(string);
        ASN1ObjectIdentifier.checkContentsLength(byArray.length);
        this.contents = byArray;
        this.identifier = string;
    }

    private ASN1ObjectIdentifier(byte[] byArray, String string) {
        this.contents = byArray;
        this.identifier = string;
    }

    public ASN1ObjectIdentifier branch(String string) {
        byte[] byArray;
        ASN1RelativeOID.checkIdentifier(string);
        if (string.length() <= 2) {
            ASN1ObjectIdentifier.checkContentsLength(this.contents.length + 1);
            int n = string.charAt(0) - 48;
            if (string.length() == 2) {
                n *= 10;
                n += string.charAt(1) - 48;
            }
            byArray = Arrays.append(this.contents, (byte)n);
        } else {
            byte[] byArray2 = ASN1RelativeOID.parseIdentifier(string);
            ASN1ObjectIdentifier.checkContentsLength(this.contents.length + byArray2.length);
            byArray = Arrays.concatenate(this.contents, byArray2);
        }
        String string2 = this.getId();
        String string3 = string2 + "." + string;
        return new ASN1ObjectIdentifier(byArray, string3);
    }

    public synchronized String getId() {
        if (this.identifier == null) {
            this.identifier = ASN1ObjectIdentifier.parseContents(this.contents);
        }
        return this.identifier;
    }

    public boolean on(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        byte[] byArray = this.contents;
        byte[] byArray2 = aSN1ObjectIdentifier.contents;
        int n = byArray2.length;
        return byArray.length > n && Arrays.areEqual(byArray, 0, n, byArray2, 0, n);
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
        aSN1OutputStream.writeEncodingDL(bl, 6, this.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (this == aSN1Primitive) {
            return true;
        }
        if (!(aSN1Primitive instanceof ASN1ObjectIdentifier)) {
            return false;
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1ObjectIdentifier.contents);
    }

    public String toString() {
        return this.getId();
    }

    static void checkContentsLength(int n) {
        if (n > 4096) {
            throw new IllegalArgumentException("exceeded OID contents length limit");
        }
    }

    static void checkIdentifier(String string) {
        if (string == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (string.length() > 16385) {
            throw new IllegalArgumentException("exceeded OID contents length limit");
        }
        if (!ASN1ObjectIdentifier.isValidIdentifier(string)) {
            throw new IllegalArgumentException("string " + string + " not a valid OID");
        }
    }

    static ASN1ObjectIdentifier createPrimitive(byte[] byArray, boolean bl) {
        ASN1ObjectIdentifier.checkContentsLength(byArray.length);
        OidHandle oidHandle = new OidHandle(byArray);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)pool.get(oidHandle);
        if (aSN1ObjectIdentifier != null) {
            return aSN1ObjectIdentifier;
        }
        if (!ASN1RelativeOID.isValidContents(byArray)) {
            throw new IllegalArgumentException("invalid OID contents");
        }
        return new ASN1ObjectIdentifier(bl ? Arrays.clone(byArray) : byArray, null);
    }

    private static boolean isValidIdentifier(String string) {
        if (string.length() < 3 || string.charAt(1) != '.') {
            return false;
        }
        char c = string.charAt(0);
        if (c < '0' || c > '2') {
            return false;
        }
        if (!ASN1RelativeOID.isValidIdentifier(string, 2)) {
            return false;
        }
        if (c == '2') {
            return true;
        }
        if (string.length() == 3 || string.charAt(3) == '.') {
            return true;
        }
        if (string.length() == 4 || string.charAt(4) == '.') {
            return string.charAt(2) < '4';
        }
        return false;
    }

    private static String parseContents(byte[] byArray) {
        StringBuilder stringBuilder = new StringBuilder();
        long l = 0L;
        BigInteger bigInteger = null;
        boolean bl = true;
        for (int i = 0; i != byArray.length; ++i) {
            int n = byArray[i] & 0xFF;
            if (l <= 0xFFFFFFFFFFFF80L) {
                l += (long)(n & 0x7F);
                if ((n & 0x80) == 0) {
                    if (bl) {
                        if (l < 40L) {
                            stringBuilder.append('0');
                        } else if (l < 80L) {
                            stringBuilder.append('1');
                            l -= 40L;
                        } else {
                            stringBuilder.append('2');
                            l -= 80L;
                        }
                        bl = false;
                    }
                    stringBuilder.append('.');
                    stringBuilder.append(l);
                    l = 0L;
                    continue;
                }
                l <<= 7;
                continue;
            }
            if (bigInteger == null) {
                bigInteger = BigInteger.valueOf(l);
            }
            bigInteger = bigInteger.or(BigInteger.valueOf(n & 0x7F));
            if ((n & 0x80) == 0) {
                if (bl) {
                    stringBuilder.append('2');
                    bigInteger = bigInteger.subtract(BigInteger.valueOf(80L));
                    bl = false;
                }
                stringBuilder.append('.');
                stringBuilder.append(bigInteger);
                bigInteger = null;
                l = 0L;
                continue;
            }
            bigInteger = bigInteger.shiftLeft(7);
        }
        return stringBuilder.toString();
    }

    private static byte[] parseIdentifier(String string) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OIDTokenizer oIDTokenizer = new OIDTokenizer(string);
        int n = Integer.parseInt(oIDTokenizer.nextToken()) * 40;
        String string2 = oIDTokenizer.nextToken();
        if (string2.length() <= 18) {
            ASN1RelativeOID.writeField(byteArrayOutputStream, (long)n + Long.parseLong(string2));
        } else {
            ASN1RelativeOID.writeField(byteArrayOutputStream, new BigInteger(string2).add(BigInteger.valueOf(n)));
        }
        while (oIDTokenizer.hasMoreTokens()) {
            String string3 = oIDTokenizer.nextToken();
            if (string3.length() <= 18) {
                ASN1RelativeOID.writeField(byteArrayOutputStream, Long.parseLong(string3));
                continue;
            }
            ASN1RelativeOID.writeField(byteArrayOutputStream, new BigInteger(string3));
        }
        return byteArrayOutputStream.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ASN1ObjectIdentifier intern() {
        OidHandle oidHandle = new OidHandle(this.contents);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)pool.get(oidHandle);
        if (aSN1ObjectIdentifier == null) {
            ConcurrentMap<OidHandle, ASN1ObjectIdentifier> concurrentMap = pool;
            synchronized (concurrentMap) {
                if (!pool.containsKey(oidHandle)) {
                    pool.put(oidHandle, this);
                    return this;
                }
                return (ASN1ObjectIdentifier)pool.get(oidHandle);
            }
        }
        return aSN1ObjectIdentifier;
    }

    static class OidHandle {
        private final int key;
        private final byte[] contents;

        OidHandle(byte[] byArray) {
            this.key = Arrays.hashCode(byArray);
            this.contents = byArray;
        }

        public int hashCode() {
            return this.key;
        }

        public boolean equals(Object object) {
            if (object instanceof OidHandle) {
                return Arrays.areEqual(this.contents, ((OidHandle)object).contents);
            }
            return false;
        }
    }
}

