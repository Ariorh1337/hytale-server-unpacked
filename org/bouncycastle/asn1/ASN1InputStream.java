/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1GeneralString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1GraphicString;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1NumericString;
import org.bouncycastle.asn1.ASN1ObjectDescriptor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1RelativeOID;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1T61String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.ASN1VideotexString;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.BERBitStringParser;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BEROctetStringParser;
import org.bouncycastle.asn1.BERSequenceParser;
import org.bouncycastle.asn1.BERSetParser;
import org.bouncycastle.asn1.BERTags;
import org.bouncycastle.asn1.DERExternalParser;
import org.bouncycastle.asn1.DLFactory;
import org.bouncycastle.asn1.DefiniteLengthInputStream;
import org.bouncycastle.asn1.IndefiniteLengthInputStream;
import org.bouncycastle.asn1.LazyEncodedSequence;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.io.Streams;

public class ASN1InputStream
extends FilterInputStream
implements BERTags {
    private final int limit;
    private final boolean lazyEvaluate;
    private final byte[][] tmpBuffers;

    public ASN1InputStream(InputStream inputStream) {
        this(inputStream, StreamUtil.findLimit(inputStream));
    }

    public ASN1InputStream(byte[] byArray) {
        this((InputStream)new ByteArrayInputStream(byArray), byArray.length);
    }

    public ASN1InputStream(byte[] byArray, boolean bl) {
        this(new ByteArrayInputStream(byArray), byArray.length, bl);
    }

    public ASN1InputStream(InputStream inputStream, int n) {
        this(inputStream, n, false);
    }

    public ASN1InputStream(InputStream inputStream, boolean bl) {
        this(inputStream, StreamUtil.findLimit(inputStream), bl);
    }

    public ASN1InputStream(InputStream inputStream, int n, boolean bl) {
        this(inputStream, n, bl, new byte[11][]);
    }

    private ASN1InputStream(InputStream inputStream, int n, boolean bl, byte[][] byArray) {
        super(inputStream);
        this.limit = n;
        this.lazyEvaluate = bl;
        this.tmpBuffers = byArray;
    }

    int getLimit() {
        return this.limit;
    }

    protected int readLength() throws IOException {
        return ASN1InputStream.readLength(this, this.limit, false);
    }

    protected void readFully(byte[] byArray) throws IOException {
        if (Streams.readFully(this, byArray, 0, byArray.length) != byArray.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }

    protected ASN1Primitive buildObject(int n, int n2, int n3) throws IOException {
        DefiniteLengthInputStream definiteLengthInputStream = new DefiniteLengthInputStream(this, n3, this.limit);
        if (0 == (n & 0xE0)) {
            return ASN1InputStream.createPrimitiveDERObject(n2, definiteLengthInputStream, this.tmpBuffers);
        }
        int n4 = n & 0xC0;
        if (0 != n4) {
            boolean bl = (n & 0x20) != 0;
            return this.readTaggedObjectDL(n4, n2, bl, definiteLengthInputStream);
        }
        switch (n2) {
            case 3: {
                return this.buildConstructedBitString(this.readVector(definiteLengthInputStream));
            }
            case 4: {
                return this.buildConstructedOctetString(this.readVector(definiteLengthInputStream));
            }
            case 16: {
                if (definiteLengthInputStream.getRemaining() < 1) {
                    return DLFactory.EMPTY_SEQUENCE;
                }
                if (this.lazyEvaluate) {
                    return new LazyEncodedSequence(definiteLengthInputStream.toByteArray());
                }
                return DLFactory.createSequence(this.readVector(definiteLengthInputStream));
            }
            case 17: {
                return DLFactory.createSet(this.readVector(definiteLengthInputStream));
            }
            case 8: {
                return DLFactory.createSequence(this.readVector(definiteLengthInputStream)).toASN1External();
            }
        }
        throw new IOException("unknown tag " + n2 + " encountered");
    }

    public ASN1Primitive readObject() throws IOException {
        int n = this.read();
        if (n <= 0) {
            if (n == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        int n2 = ASN1InputStream.readTagNumber(this, n);
        int n3 = this.readLength();
        if (n3 >= 0) {
            try {
                return this.buildObject(n, n2, n3);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new ASN1Exception("corrupted stream detected", illegalArgumentException);
            }
        }
        if (0 == (n & 0x20)) {
            throw new IOException("indefinite-length primitive encoding encountered");
        }
        IndefiniteLengthInputStream indefiniteLengthInputStream = new IndefiniteLengthInputStream(this, this.limit);
        ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(indefiniteLengthInputStream, this.limit, this.tmpBuffers);
        int n4 = n & 0xC0;
        if (0 != n4) {
            return aSN1StreamParser.loadTaggedIL(n4, n2);
        }
        switch (n2) {
            case 3: {
                return BERBitStringParser.parse(aSN1StreamParser);
            }
            case 4: {
                return BEROctetStringParser.parse(aSN1StreamParser);
            }
            case 8: {
                return DERExternalParser.parse(aSN1StreamParser);
            }
            case 16: {
                return BERSequenceParser.parse(aSN1StreamParser);
            }
            case 17: {
                return BERSetParser.parse(aSN1StreamParser);
            }
        }
        throw new IOException("unknown BER object encountered");
    }

    ASN1BitString buildConstructedBitString(ASN1EncodableVector aSN1EncodableVector) throws IOException {
        ASN1BitString[] aSN1BitStringArray = new ASN1BitString[aSN1EncodableVector.size()];
        for (int i = 0; i != aSN1BitStringArray.length; ++i) {
            ASN1Encodable aSN1Encodable = aSN1EncodableVector.get(i);
            if (!(aSN1Encodable instanceof ASN1BitString)) {
                throw new ASN1Exception("unknown object encountered in constructed BIT STRING: " + aSN1Encodable.getClass());
            }
            aSN1BitStringArray[i] = (ASN1BitString)aSN1Encodable;
        }
        return new BERBitString(aSN1BitStringArray);
    }

    ASN1OctetString buildConstructedOctetString(ASN1EncodableVector aSN1EncodableVector) throws IOException {
        ASN1OctetString[] aSN1OctetStringArray = new ASN1OctetString[aSN1EncodableVector.size()];
        for (int i = 0; i != aSN1OctetStringArray.length; ++i) {
            ASN1Encodable aSN1Encodable = aSN1EncodableVector.get(i);
            if (!(aSN1Encodable instanceof ASN1OctetString)) {
                throw new ASN1Exception("unknown object encountered in constructed OCTET STRING: " + aSN1Encodable.getClass());
            }
            aSN1OctetStringArray[i] = (ASN1OctetString)aSN1Encodable;
        }
        return new BEROctetString(aSN1OctetStringArray);
    }

    ASN1Primitive readTaggedObjectDL(int n, int n2, boolean bl, DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        if (!bl) {
            byte[] byArray = definiteLengthInputStream.toByteArray();
            return ASN1TaggedObject.createPrimitive(n, n2, byArray);
        }
        ASN1EncodableVector aSN1EncodableVector = this.readVector(definiteLengthInputStream);
        return ASN1TaggedObject.createConstructedDL(n, n2, aSN1EncodableVector);
    }

    ASN1EncodableVector readVector() throws IOException {
        ASN1Primitive aSN1Primitive = this.readObject();
        if (null == aSN1Primitive) {
            return new ASN1EncodableVector(0);
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        do {
            aSN1EncodableVector.add(aSN1Primitive);
        } while ((aSN1Primitive = this.readObject()) != null);
        return aSN1EncodableVector;
    }

    ASN1EncodableVector readVector(DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        int n = definiteLengthInputStream.getRemaining();
        if (n < 1) {
            return new ASN1EncodableVector(0);
        }
        return new ASN1InputStream(definiteLengthInputStream, n, this.lazyEvaluate, this.tmpBuffers).readVector();
    }

    static int readTagNumber(InputStream inputStream, int n) throws IOException {
        int n2 = n & 0x1F;
        if (n2 == 31) {
            int n3 = inputStream.read();
            if (n3 < 31) {
                if (n3 < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                throw new IOException("corrupted stream - high tag number < 31 found");
            }
            n2 = n3 & 0x7F;
            if (0 == n2) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while ((n3 & 0x80) != 0) {
                if (n2 >>> 24 != 0) {
                    throw new IOException("Tag number more than 31 bits");
                }
                n2 <<= 7;
                n3 = inputStream.read();
                if (n3 < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                n2 |= n3 & 0x7F;
            }
        }
        return n2;
    }

    static int readLength(InputStream inputStream, int n, boolean bl) throws IOException {
        int n2 = inputStream.read();
        if (0 == n2 >>> 7) {
            return n2;
        }
        if (128 == n2) {
            return -1;
        }
        if (n2 < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (255 == n2) {
            throw new IOException("invalid long form definite-length 0xFF");
        }
        int n3 = n2 & 0x7F;
        int n4 = 0;
        n2 = 0;
        do {
            int n5;
            if ((n5 = inputStream.read()) < 0) {
                throw new EOFException("EOF found reading length");
            }
            if (n2 >>> 23 != 0) {
                throw new IOException("long form definite-length more than 31 bits");
            }
            n2 = (n2 << 8) + n5;
        } while (++n4 < n3);
        if (n2 >= n && !bl) {
            throw new IOException("corrupted stream - out of bounds length found: " + n2 + " >= " + n);
        }
        return n2;
    }

    private static byte[] getBuffer(DefiniteLengthInputStream definiteLengthInputStream, byte[][] byArray) throws IOException {
        int n = definiteLengthInputStream.getRemaining();
        if (n >= byArray.length) {
            return definiteLengthInputStream.toByteArray();
        }
        byte[] byArray2 = byArray[n];
        if (byArray2 == null) {
            byArray[n] = new byte[n];
            byArray2 = byArray[n];
        }
        definiteLengthInputStream.readAllIntoByteArray(byArray2);
        return byArray2;
    }

    private static char[] getBMPCharBuffer(DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        int n = definiteLengthInputStream.getRemaining();
        if (0 != (n & 1)) {
            throw new IOException("malformed BMPString encoding encountered");
        }
        char[] cArray = new char[n / 2];
        int n2 = 0;
        byte[] byArray = new byte[8];
        while (n >= 8) {
            if (Streams.readFully(definiteLengthInputStream, byArray, 0, 8) != 8) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            cArray[n2] = (char)(byArray[0] << 8 | byArray[1] & 0xFF);
            cArray[n2 + 1] = (char)(byArray[2] << 8 | byArray[3] & 0xFF);
            cArray[n2 + 2] = (char)(byArray[4] << 8 | byArray[5] & 0xFF);
            cArray[n2 + 3] = (char)(byArray[6] << 8 | byArray[7] & 0xFF);
            n2 += 4;
            n -= 8;
        }
        if (n > 0) {
            if (Streams.readFully(definiteLengthInputStream, byArray, 0, n) != n) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            int n3 = 0;
            do {
                int n4 = byArray[n3++] << 8;
                int n5 = byArray[n3++] & 0xFF;
                cArray[n2++] = (char)(n4 | n5);
            } while (n3 < n);
        }
        if (0 != definiteLengthInputStream.getRemaining() || cArray.length != n2) {
            throw new IllegalStateException();
        }
        return cArray;
    }

    static ASN1Primitive createPrimitiveDERObject(int n, DefiniteLengthInputStream definiteLengthInputStream, byte[][] byArray) throws IOException {
        try {
            switch (n) {
                case 3: {
                    return ASN1BitString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 30: {
                    return ASN1BMPString.createPrimitive(ASN1InputStream.getBMPCharBuffer(definiteLengthInputStream));
                }
                case 1: {
                    return ASN1Boolean.createPrimitive(ASN1InputStream.getBuffer(definiteLengthInputStream, byArray));
                }
                case 10: {
                    return ASN1Enumerated.createPrimitive(ASN1InputStream.getBuffer(definiteLengthInputStream, byArray), true);
                }
                case 27: {
                    return ASN1GeneralString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 24: {
                    return ASN1GeneralizedTime.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 25: {
                    return ASN1GraphicString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 22: {
                    return ASN1IA5String.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 2: {
                    return ASN1Integer.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 5: {
                    ASN1Null.checkContentsLength(definiteLengthInputStream.getRemaining());
                    return ASN1Null.createPrimitive();
                }
                case 18: {
                    return ASN1NumericString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 7: {
                    return ASN1ObjectDescriptor.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 6: {
                    ASN1ObjectIdentifier.checkContentsLength(definiteLengthInputStream.getRemaining());
                    return ASN1ObjectIdentifier.createPrimitive(ASN1InputStream.getBuffer(definiteLengthInputStream, byArray), true);
                }
                case 4: {
                    return ASN1OctetString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 19: {
                    return ASN1PrintableString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 13: {
                    ASN1RelativeOID.checkContentsLength(definiteLengthInputStream.getRemaining());
                    return ASN1RelativeOID.createPrimitive(ASN1InputStream.getBuffer(definiteLengthInputStream, byArray), true);
                }
                case 20: {
                    return ASN1T61String.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 28: {
                    return ASN1UniversalString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 23: {
                    return ASN1UTCTime.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 12: {
                    return ASN1UTF8String.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 21: {
                    return ASN1VideotexString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 26: {
                    return ASN1VisibleString.createPrimitive(definiteLengthInputStream.toByteArray());
                }
                case 14: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: {
                    throw new IOException("unsupported tag " + n + " encountered");
                }
            }
            throw new IOException("unknown tag " + n + " encountered");
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new ASN1Exception(illegalArgumentException.getMessage(), illegalArgumentException);
        }
        catch (IllegalStateException illegalStateException) {
            throw new ASN1Exception(illegalStateException.getMessage(), illegalStateException);
        }
    }
}

