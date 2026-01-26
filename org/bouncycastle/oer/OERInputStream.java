/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

public class OERInputStream
extends FilterInputStream {
    private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
    private static final int[] bitsR = new int[]{128, 64, 32, 16, 8, 4, 2, 1};
    protected PrintWriter debugOutput = null;
    private int maxByteAllocation = 0x100000;
    protected PrintWriter debugStream = null;

    public OERInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public OERInputStream(InputStream inputStream, int n) {
        super(inputStream);
        this.maxByteAllocation = n;
    }

    public static ASN1Encodable parse(byte[] byArray, Element element) throws IOException {
        OERInputStream oERInputStream = new OERInputStream(new ByteArrayInputStream(byArray));
        return oERInputStream.parse(element);
    }

    private int countOptionalChildTypes(Element element) {
        int n = 0;
        for (Element element2 : element.getChildren()) {
            n += element2.isExplicit() ? 0 : 1;
        }
        return n;
    }

    public ASN1Object parse(Element element) throws IOException {
        switch (element.getBaseType()) {
            case OPAQUE: {
                ElementSupplier elementSupplier = element.resolveSupplier();
                return this.parse(new Element(elementSupplier.build(), element));
            }
            case Switch: {
                throw new IllegalStateException("A switch element should only be found within a sequence.");
            }
            case Supplier: {
                return this.parse(new Element(element.getElementSupplier().build(), element));
            }
            case SEQ_OF: {
                int n = this.readLength().intLength();
                byte[] byArray = this.allocateArray(n);
                if (Streams.readFully(this, byArray) != byArray.length) {
                    throw new IOException("could not read all of count of seq-of values");
                }
                int n2 = BigIntegers.fromUnsignedByteArray(byArray).intValue();
                this.debugPrint(element + "(len = " + n2 + ")");
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                if (element.getChildren().get(0).getaSwitch() != null) {
                    throw new IllegalStateException("element def for item in SEQ OF has a switch, switches only supported in sequences");
                }
                for (int i = 0; i < n2; ++i) {
                    Element element2 = Element.expandDeferredDefinition(element.getChildren().get(0), element);
                    aSN1EncodableVector.add(this.parse(element2));
                }
                return new DERSequence(aSN1EncodableVector);
            }
            case SEQ: {
                Object object;
                Sequence sequence = new Sequence(this.in, element);
                this.debugPrint(element + sequence.toString());
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                List<Element> list = element.getChildren();
                int n = 0;
                boolean bl = false;
                for (n = 0; n < list.size(); ++n) {
                    Element element3 = list.get(n);
                    if (element3.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (element3.getBlock() > 0) break;
                    if ((element3 = Element.expandDeferredDefinition(element3, element)).getaSwitch() != null) {
                        object = element3.getaSwitch().result(new SwitchIndexer.Asn1EncodableVectorIndexer(aSN1EncodableVector));
                        if (object.getParent() != element) {
                            object = new Element((Element)object, element);
                        }
                    } else {
                        object = element3;
                    }
                    if (sequence.valuePresent == null) {
                        aSN1EncodableVector.add(this.parse((Element)object));
                        continue;
                    }
                    if (sequence.valuePresent[n]) {
                        if (object.isExplicit()) {
                            aSN1EncodableVector.add(this.parse((Element)object));
                            continue;
                        }
                        aSN1EncodableVector.add(OEROptional.getInstance(this.parse((Element)object)));
                        continue;
                    }
                    if (object.getDefaultValue() != null) {
                        aSN1EncodableVector.add(element3.getDefaultValue());
                        continue;
                    }
                    aSN1EncodableVector.add(this.absent(element3));
                }
                if (sequence.extensionFlagSet) {
                    int n3 = this.readLength().intLength();
                    object = this.allocateArray(n3);
                    if (Streams.readFully(this.in, object) != ((byte[])object).length) {
                        throw new IOException("did not fully read presence list.");
                    }
                    int n4 = ((byte[])object).length * 8 - object[0];
                    for (int i = 8; n < list.size() || i < n4; ++i, ++n) {
                        Element element4;
                        Element element5 = element4 = n < list.size() ? list.get(n) : null;
                        if (element4 == null) {
                            if ((object[i / 8] & bitsR[i % 8]) == 0) continue;
                            int n5 = this.readLength().intLength();
                            while (--n5 >= 0) {
                                this.in.read();
                            }
                            continue;
                        }
                        if (i < n4 && (object[i / 8] & bitsR[i % 8]) != 0) {
                            aSN1EncodableVector.add(this.parseOpenType(element4));
                            continue;
                        }
                        if (element4.isExplicit()) {
                            throw new IOException("extension is marked as explicit but is not defined in presence list");
                        }
                        aSN1EncodableVector.add(OEROptional.ABSENT);
                    }
                }
                return new DERSequence(aSN1EncodableVector);
            }
            case CHOICE: {
                Choice choice = this.choice();
                this.debugPrint(choice.toString() + " " + choice.tag);
                if (choice.isContextSpecific()) {
                    Element element6 = Element.expandDeferredDefinition(element.getChildren().get(choice.getTag()), element);
                    if (element6.getBlock() > 0) {
                        this.debugPrint("Chosen (Ext): " + element6);
                        return new DERTaggedObject(choice.tag, this.parseOpenType(element6));
                    }
                    this.debugPrint("Chosen: " + element6);
                    return new DERTaggedObject(choice.tag, this.parse(element6));
                }
                if (choice.isApplicationTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isPrivateTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isUniversalTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                throw new IllegalStateException("Unimplemented tag type");
            }
            case ENUM: {
                BigInteger bigInteger = this.enumeration();
                this.debugPrint(element + "ENUM(" + bigInteger + ") = " + element.getChildren().get(bigInteger.intValue()).getLabel());
                return new ASN1Enumerated(bigInteger);
            }
            case INT: {
                BigInteger bigInteger;
                byte[] byArray;
                int n = element.intBytesForRange();
                if (n != 0) {
                    byArray = this.allocateArray(Math.abs(n));
                    Streams.readFully(this, byArray);
                    bigInteger = n < 0 ? new BigInteger(byArray) : BigIntegers.fromUnsignedByteArray(byArray);
                } else if (element.isLowerRangeZero()) {
                    LengthInfo lengthInfo = this.readLength();
                    byArray = this.allocateArray(lengthInfo.intLength());
                    Streams.readFully(this, byArray);
                    bigInteger = byArray.length == 0 ? BigInteger.ZERO : new BigInteger(1, byArray);
                } else {
                    LengthInfo lengthInfo = this.readLength();
                    byArray = this.allocateArray(lengthInfo.intLength());
                    Streams.readFully(this, byArray);
                    bigInteger = byArray.length == 0 ? BigInteger.ZERO : new BigInteger(byArray);
                }
                if (this.debugOutput != null) {
                    this.debugPrint(element + "INTEGER byteLen= " + byArray.length + " hex= " + bigInteger.toString(16) + ")");
                }
                return new ASN1Integer(bigInteger);
            }
            case OCTET_STRING: {
                int n = 0;
                n = element.getUpperBound() != null && element.getUpperBound().equals(element.getLowerBound()) ? element.getUpperBound().intValue() : this.readLength().intLength();
                byte[] byArray = this.allocateArray(n);
                if (Streams.readFully(this, byArray) != n) {
                    throw new IOException("did not read all of " + element.getLabel());
                }
                if (this.debugOutput != null) {
                    int n6 = Math.min(byArray.length, 32);
                    this.debugPrint(element + "OCTET STRING (" + byArray.length + ") = " + Hex.toHexString(byArray, 0, n6) + " " + (byArray.length > 32 ? "..." : ""));
                }
                return new DEROctetString(byArray);
            }
            case IA5String: {
                byte[] byArray = element.isFixedLength() ? this.allocateArray(element.getUpperBound().intValue()) : this.allocateArray(this.readLength().intLength());
                if (Streams.readFully(this, byArray) != byArray.length) {
                    throw new IOException("could not read all of IA5 string");
                }
                String string = Strings.fromByteArray(byArray);
                if (this.debugOutput != null) {
                    this.debugPrint(element.appendLabel("IA5 String (" + byArray.length + ") = " + string));
                }
                return new DERIA5String(string);
            }
            case UTF8_STRING: {
                byte[] byArray = this.allocateArray(this.readLength().intLength());
                if (Streams.readFully(this, byArray) != byArray.length) {
                    throw new IOException("could not read all of utf 8 string");
                }
                String string = Strings.fromUTF8ByteArray(byArray);
                if (this.debugOutput != null) {
                    this.debugPrint(element + "UTF8 String (" + byArray.length + ") = " + string);
                }
                return new DERUTF8String(string);
            }
            case BIT_STRING: {
                byte[] byArray = element.isFixedLength() ? new byte[element.getLowerBound().intValue() / 8] : (BigInteger.ZERO.compareTo(element.getUpperBound()) > 0 ? this.allocateArray(element.getUpperBound().intValue() / 8) : this.allocateArray(this.readLength().intLength() / 8));
                Streams.readFully(this, byArray);
                if (this.debugOutput != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("BIT STRING(" + byArray.length * 8 + ") = ");
                    for (int i = 0; i != byArray.length; ++i) {
                        byte by = byArray[i];
                        for (int j = 0; j < 8; ++j) {
                            stringBuffer.append((by & 0x80) > 0 ? "1" : "0");
                            by = (byte)(by << 1);
                        }
                    }
                    this.debugPrint(element + stringBuffer.toString());
                }
                return new DERBitString(byArray);
            }
            case NULL: {
                this.debugPrint(element + "NULL");
                return DERNull.INSTANCE;
            }
            case EXTENSION: {
                LengthInfo lengthInfo = this.readLength();
                byte[] byArray = new byte[lengthInfo.intLength()];
                if (Streams.readFully(this, byArray) != lengthInfo.intLength()) {
                    throw new IOException("could not read all of count of open value in choice (...) ");
                }
                this.debugPrint("ext " + lengthInfo.intLength() + " " + Hex.toHexString(byArray));
                return new DEROctetString(byArray);
            }
            case BOOLEAN: {
                if (this.read() == 0) {
                    return ASN1Boolean.FALSE;
                }
                return ASN1Boolean.TRUE;
            }
        }
        throw new IllegalStateException("Unhandled type " + (Object)((Object)element.getBaseType()));
    }

    private ASN1Encodable absent(Element element) {
        this.debugPrint(element + "Absent");
        return OEROptional.ABSENT;
    }

    private byte[] allocateArray(int n) {
        if (n > this.maxByteAllocation) {
            throw new IllegalArgumentException("required byte array size " + n + " was greater than " + this.maxByteAllocation);
        }
        return new byte[n];
    }

    public BigInteger parseInt(boolean bl, int n) throws Exception {
        byte[] byArray = new byte[n];
        int n2 = Streams.readFully(this, byArray);
        if (n2 != byArray.length) {
            throw new IllegalStateException("integer not fully read");
        }
        return bl ? new BigInteger(1, byArray) : new BigInteger(byArray);
    }

    public BigInteger uint8() throws Exception {
        return this.parseInt(true, 1);
    }

    public BigInteger uint16() throws Exception {
        return this.parseInt(true, 2);
    }

    public BigInteger uint32() throws Exception {
        return this.parseInt(true, 4);
    }

    public BigInteger uint64() throws Exception {
        return this.parseInt(false, 8);
    }

    public BigInteger int8() throws Exception {
        return this.parseInt(false, 1);
    }

    public BigInteger int16() throws Exception {
        return this.parseInt(false, 2);
    }

    public BigInteger int32() throws Exception {
        return this.parseInt(false, 4);
    }

    public BigInteger int64() throws Exception {
        return this.parseInt(false, 8);
    }

    public LengthInfo readLength() throws IOException {
        boolean bl = false;
        int n = this.read();
        if (n == -1) {
            throw new EOFException("expecting length");
        }
        if ((n & 0x80) == 0) {
            this.debugPrint("Len (Short form): " + (n & 0x7F));
            return new LengthInfo(BigInteger.valueOf(n & 0x7F), true);
        }
        byte[] byArray = new byte[n & 0x7F];
        if (Streams.readFully(this, byArray) != byArray.length) {
            throw new EOFException("did not read all bytes of length definition");
        }
        this.debugPrint("Len (Long Form): " + (n & 0x7F) + " actual len: " + Hex.toHexString(byArray));
        return new LengthInfo(BigIntegers.fromUnsignedByteArray(byArray), false);
    }

    public BigInteger enumeration() throws IOException {
        int n = this.read();
        if (n == -1) {
            throw new EOFException("expecting prefix of enumeration");
        }
        if ((n & 0x80) == 128) {
            int n2 = n & 0x7F;
            if (n2 == 0) {
                return BigInteger.ZERO;
            }
            byte[] byArray = new byte[n2];
            int n3 = Streams.readFully(this, byArray);
            if (n3 != byArray.length) {
                throw new EOFException("unable to fully read integer component of enumeration");
            }
            return new BigInteger(1, byArray);
        }
        return BigInteger.valueOf(n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ASN1Encodable parseOpenType(Element element) throws IOException {
        int n = this.readLength().intLength();
        byte[] byArray = this.allocateArray(n);
        if (Streams.readFully(this.in, byArray) != byArray.length) {
            throw new IOException("did not fully read open type as raw bytes");
        }
        try (FilterInputStream filterInputStream = null;){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byArray);
            filterInputStream = new OERInputStream(byteArrayInputStream);
            ASN1Object aSN1Object = ((OERInputStream)filterInputStream).parse(element);
            return aSN1Object;
        }
    }

    public Choice choice() throws IOException {
        return new Choice(this);
    }

    protected void debugPrint(String string) {
        if (this.debugOutput != null) {
            StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
            int n = -1;
            for (int i = 0; i != stackTraceElementArray.length; ++i) {
                StackTraceElement stackTraceElement = stackTraceElementArray[i];
                if (stackTraceElement.getMethodName().equals("debugPrint")) {
                    n = 0;
                    continue;
                }
                if (!stackTraceElement.getClassName().contains("OERInput")) continue;
                ++n;
            }
            while (n > 0) {
                this.debugOutput.append("    ");
                --n;
            }
            this.debugOutput.append(string).append("\n");
            this.debugOutput.flush();
        }
    }

    public static class Choice
    extends OERInputStream {
        final int preamble = this.read();
        final int tag;
        final int tagClass;

        public Choice(InputStream inputStream) throws IOException {
            super(inputStream);
            if (this.preamble < 0) {
                throw new EOFException("expecting preamble byte of choice");
            }
            this.tagClass = this.preamble & 0xC0;
            int n = this.preamble & 0x3F;
            if (n >= 63) {
                n = 0;
                int n2 = 0;
                do {
                    if ((n2 = inputStream.read()) < 0) {
                        throw new EOFException("expecting further tag bytes");
                    }
                    n <<= 7;
                    n |= n2 & 0x7F;
                } while ((n2 & 0x80) != 0);
            }
            this.tag = n;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CHOICE(");
            switch (this.tagClass) {
                case 0: {
                    stringBuilder.append("Universal ");
                    break;
                }
                case 64: {
                    stringBuilder.append("Application ");
                    break;
                }
                case 192: {
                    stringBuilder.append("Private ");
                    break;
                }
                case 128: {
                    stringBuilder.append("ContextSpecific ");
                }
            }
            stringBuilder.append("Tag = " + this.tag);
            stringBuilder.append(")");
            return stringBuilder.toString();
        }

        public int getTagClass() {
            return this.tagClass;
        }

        public int getTag() {
            return this.tag;
        }

        public boolean isContextSpecific() {
            return this.tagClass == 128;
        }

        public boolean isUniversalTagClass() {
            return this.tagClass == 0;
        }

        public boolean isApplicationTagClass() {
            return this.tagClass == 64;
        }

        public boolean isPrivateTagClass() {
            return this.tagClass == 192;
        }
    }

    private static final class LengthInfo {
        private final BigInteger length;
        private final boolean shortForm;

        public LengthInfo(BigInteger bigInteger, boolean bl) {
            this.length = bigInteger;
            this.shortForm = bl;
        }

        private int intLength() {
            return BigIntegers.intValueExact(this.length);
        }
    }

    public static class Sequence
    extends OERInputStream {
        private final int preamble;
        private final boolean[] valuePresent;
        private final boolean extensionFlagSet;

        public Sequence(InputStream inputStream, Element element) throws IOException {
            super(inputStream);
            if (element.hasPopulatedExtension() || element.getOptionals() > 0 || element.hasDefaultChildren()) {
                this.preamble = this.in.read();
                if (this.preamble < 0) {
                    throw new EOFException("expecting preamble byte of sequence");
                }
            } else {
                this.preamble = 0;
                this.extensionFlagSet = false;
                this.valuePresent = null;
                return;
            }
            this.extensionFlagSet = element.hasPopulatedExtension() && (this.preamble & 0x80) == 128;
            this.valuePresent = new boolean[element.getChildren().size()];
            int n = 0;
            int n2 = element.hasPopulatedExtension() ? 6 : 7;
            int n3 = this.preamble;
            int n4 = 0;
            for (Element element2 : element.getChildren()) {
                if (element2.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                if (element2.getBlock() != n) break;
                if (element2.isExplicit()) {
                    this.valuePresent[n4++] = true;
                    continue;
                }
                if (n2 < 0) {
                    n3 = inputStream.read();
                    if (n3 < 0) {
                        throw new EOFException("expecting mask byte sequence");
                    }
                    n2 = 7;
                }
                this.valuePresent[n4++] = (n3 & bits[n2]) > 0;
                --n2;
            }
        }

        public boolean hasOptional(int n) {
            return this.valuePresent[n];
        }

        public boolean hasExtension() {
            return this.extensionFlagSet;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SEQ(");
            stringBuilder.append(this.hasExtension() ? "Ext " : "");
            if (this.valuePresent == null) {
                stringBuilder.append("*");
            } else {
                for (int i = 0; i < this.valuePresent.length; ++i) {
                    if (this.valuePresent[i]) {
                        stringBuilder.append("1");
                        continue;
                    }
                    stringBuilder.append("0");
                }
            }
            stringBuilder.append(")");
            return stringBuilder.toString();
        }
    }
}

