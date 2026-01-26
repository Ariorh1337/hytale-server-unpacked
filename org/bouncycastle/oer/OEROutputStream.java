/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.oer.BitBuilder;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class OEROutputStream
extends OutputStream {
    private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
    private final OutputStream out;
    protected PrintWriter debugOutput = null;

    public OEROutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    public static int byteLength(long l) {
        int n;
        long l2 = -72057594037927936L;
        for (n = 8; n > 0 && (l & l2) == 0L; --n) {
            l <<= 8;
        }
        return n;
    }

    public void write(ASN1Encodable aSN1Encodable, Element element) throws IOException {
        if (aSN1Encodable == OEROptional.ABSENT) {
            return;
        }
        if (aSN1Encodable instanceof OEROptional) {
            this.write(((OEROptional)aSN1Encodable).get(), element);
            return;
        }
        aSN1Encodable = aSN1Encodable.toASN1Primitive();
        switch (element.getBaseType()) {
            case Supplier: {
                this.write(aSN1Encodable, element.getElementSupplier().build());
                break;
            }
            case SEQ: {
                int n;
                Object object;
                Object object2;
                Element element2;
                int n2;
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Encodable);
                int n3 = 7;
                int n4 = 0;
                boolean bl = false;
                if (element.isExtensionsInDefinition()) {
                    for (n2 = 0; n2 < element.getChildren().size() && (element2 = element.getChildren().get(n2)).getBaseType() != OERDefinition.BaseType.EXTENSION; ++n2) {
                        if (element2.getBlock() <= 0 || n2 >= aSN1Sequence.size() || OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(n2))) continue;
                        bl = true;
                        break;
                    }
                    if (bl) {
                        n4 |= bits[n3];
                    }
                    --n3;
                }
                for (n2 = 0; n2 < element.getChildren().size(); ++n2) {
                    element2 = element.getChildren().get(n2);
                    if (element2.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (element2.getBlock() > 0) break;
                    element2 = Element.expandDeferredDefinition(element2, element);
                    if (element.getaSwitch() != null) {
                        element2 = element.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(aSN1Sequence));
                        element2 = Element.expandDeferredDefinition(element2, element);
                    }
                    if (n3 < 0) {
                        this.out.write(n4);
                        n3 = 7;
                        n4 = 0;
                    }
                    object2 = aSN1Sequence.getObjectAt(n2);
                    if (element2.isExplicit() && object2 instanceof OEROptional) {
                        throw new IllegalStateException("absent sequence element that is required by oer definition");
                    }
                    if (element2.isExplicit()) continue;
                    object = aSN1Sequence.getObjectAt(n2);
                    if (element2.getDefaultValue() != null) {
                        if (object instanceof OEROptional) {
                            if (((OEROptional)object).isDefined() && !((OEROptional)object).get().equals(element2.getDefaultValue())) {
                                n4 |= bits[n3];
                            }
                        } else if (!element2.getDefaultValue().equals(object)) {
                            n4 |= bits[n3];
                        }
                    } else if (object2 != OEROptional.ABSENT) {
                        n4 |= bits[n3];
                    }
                    --n3;
                }
                if (n3 != 7) {
                    this.out.write(n4);
                }
                List<Element> list = element.getChildren();
                for (n = 0; n < list.size(); ++n) {
                    object2 = element.getChildren().get(n);
                    if (((Element)object2).getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (((Element)object2).getBlock() > 0) break;
                    object = aSN1Sequence.getObjectAt(n);
                    if (((Element)object2).getaSwitch() != null) {
                        object2 = ((Element)object2).getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(aSN1Sequence));
                    }
                    if (((Element)object2).getDefaultValue() != null && ((Element)object2).getDefaultValue().equals(object)) continue;
                    this.write((ASN1Encodable)object, (Element)object2);
                }
                if (bl) {
                    int n5 = n;
                    object = new ByteArrayOutputStream();
                    n3 = 7;
                    n4 = 0;
                    for (int i = n5; i < list.size(); ++i) {
                        if (n3 < 0) {
                            ((ByteArrayOutputStream)object).write(n4);
                            n3 = 7;
                            n4 = 0;
                        }
                        if (i < aSN1Sequence.size() && !OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(i))) {
                            n4 |= bits[n3];
                        }
                        --n3;
                    }
                    if (n3 != 7) {
                        ((ByteArrayOutputStream)object).write(n4);
                    }
                    this.encodeLength(((ByteArrayOutputStream)object).size() + 1);
                    if (n3 == 7) {
                        this.write(0);
                    } else {
                        this.write(n3 + 1);
                    }
                    this.write(((ByteArrayOutputStream)object).toByteArray());
                    while (n < list.size()) {
                        if (n < aSN1Sequence.size() && !OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(n))) {
                            this.writePlainType(aSN1Sequence.getObjectAt(n), list.get(n));
                        }
                        ++n;
                    }
                }
                this.out.flush();
                this.debugPrint(element.appendLabel(""));
                break;
            }
            case SEQ_OF: {
                Enumeration enumeration;
                if (aSN1Encodable instanceof ASN1Set) {
                    enumeration = ((ASN1Set)aSN1Encodable).getObjects();
                    this.encodeQuantity(((ASN1Set)aSN1Encodable).size());
                } else if (aSN1Encodable instanceof ASN1Sequence) {
                    enumeration = ((ASN1Sequence)aSN1Encodable).getObjects();
                    this.encodeQuantity(((ASN1Sequence)aSN1Encodable).size());
                } else {
                    throw new IllegalStateException("encodable at for SEQ_OF is not a container");
                }
                Element element3 = Element.expandDeferredDefinition(element.getFirstChid(), element);
                while (enumeration.hasMoreElements()) {
                    Object e = enumeration.nextElement();
                    this.write((ASN1Encodable)e, element3);
                }
                this.out.flush();
                this.debugPrint(element.appendLabel(""));
                break;
            }
            case CHOICE: {
                ASN1Primitive aSN1Primitive = aSN1Encodable.toASN1Primitive();
                BitBuilder bitBuilder = new BitBuilder();
                ASN1Primitive aSN1Primitive2 = null;
                if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
                    throw new IllegalStateException("only support tagged objects");
                }
                Object object = (ASN1TaggedObject)aSN1Primitive;
                int n = ((ASN1TaggedObject)object).getTagClass();
                bitBuilder.writeBit(n & 0x80).writeBit(n & 0x40);
                int n6 = ((ASN1TaggedObject)object).getTagNo();
                aSN1Primitive2 = ((ASN1TaggedObject)object).getBaseObject().toASN1Primitive();
                if (n6 <= 63) {
                    bitBuilder.writeBits(n6, 6);
                } else {
                    bitBuilder.writeBits(255L, 6);
                    bitBuilder.write7BitBytes(n6);
                }
                if (this.debugOutput != null && aSN1Primitive instanceof ASN1TaggedObject) {
                    object = (ASN1TaggedObject)aSN1Primitive;
                    switch (((ASN1TaggedObject)object).getTagClass()) {
                        case 64: {
                            this.debugPrint(element.appendLabel("AS"));
                            break;
                        }
                        case 128: {
                            this.debugPrint(element.appendLabel("CS"));
                            break;
                        }
                        case 192: {
                            this.debugPrint(element.appendLabel("PR"));
                        }
                    }
                }
                bitBuilder.writeAndClear(this.out);
                object = element.getChildren().get(n6);
                object = Element.expandDeferredDefinition((Element)object, element);
                if (((Element)object).getBlock() > 0) {
                    this.writePlainType(aSN1Primitive2, (Element)object);
                } else {
                    this.write(aSN1Primitive2, (Element)object);
                }
                this.out.flush();
                break;
            }
            case ENUM: {
                BigInteger bigInteger = aSN1Encodable instanceof ASN1Integer ? ASN1Integer.getInstance(aSN1Encodable).getValue() : ASN1Enumerated.getInstance(aSN1Encodable).getValue();
                for (Element element4 : element.getChildren()) {
                    if (!(element4 = Element.expandDeferredDefinition(element4, element)).getEnumValue().equals(bigInteger)) continue;
                    if (bigInteger.compareTo(BigInteger.valueOf(127L)) > 0) {
                        byte[] byArray = bigInteger.toByteArray();
                        int n = 0x80 | byArray.length & 0xFF;
                        this.out.write(n);
                        this.out.write(byArray);
                    } else {
                        this.out.write(bigInteger.intValue() & 0x7F);
                    }
                    this.out.flush();
                    this.debugPrint(element.appendLabel(element.rangeExpression()));
                    return;
                }
                throw new IllegalArgumentException("enum value " + bigInteger + " " + Hex.toHexString(bigInteger.toByteArray()) + " no in defined child list");
            }
            case INT: {
                ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Encodable);
                int n = element.intBytesForRange();
                if (n > 0) {
                    byte[] byArray = BigIntegers.asUnsignedByteArray(n, aSN1Integer.getValue());
                    switch (n) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 8: {
                            this.out.write(byArray);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("unknown uint length " + n);
                        }
                    }
                } else if (n < 0) {
                    byte[] byArray;
                    BigInteger bigInteger = aSN1Integer.getValue();
                    switch (n) {
                        case -1: {
                            byArray = new byte[]{BigIntegers.byteValueExact(bigInteger)};
                            break;
                        }
                        case -2: {
                            byArray = Pack.shortToBigEndian(BigIntegers.shortValueExact(bigInteger));
                            break;
                        }
                        case -4: {
                            byArray = Pack.intToBigEndian(BigIntegers.intValueExact(bigInteger));
                            break;
                        }
                        case -8: {
                            byArray = Pack.longToBigEndian(BigIntegers.longValueExact(bigInteger));
                            break;
                        }
                        default: {
                            throw new IllegalStateException("unknown twos compliment length");
                        }
                    }
                    this.out.write(byArray);
                } else {
                    byte[] byArray = element.isLowerRangeZero() ? BigIntegers.asUnsignedByteArray(aSN1Integer.getValue()) : aSN1Integer.getValue().toByteArray();
                    this.encodeLength(byArray.length);
                    this.out.write(byArray);
                }
                this.debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                break;
            }
            case OCTET_STRING: {
                ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(aSN1Encodable);
                byte[] byArray = aSN1OctetString.getOctets();
                if (element.isFixedLength()) {
                    this.out.write(byArray);
                } else {
                    this.encodeLength(byArray.length);
                    this.out.write(byArray);
                }
                this.debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                break;
            }
            case IA5String: {
                ASN1IA5String aSN1IA5String = ASN1IA5String.getInstance(aSN1Encodable);
                byte[] byArray = aSN1IA5String.getOctets();
                if (element.isFixedLength() && element.getUpperBound().intValue() != byArray.length) {
                    throw new IOException("IA5String string length does not equal declared fixed length " + byArray.length + " " + element.getUpperBound());
                }
                if (element.isFixedLength()) {
                    this.out.write(byArray);
                } else {
                    this.encodeLength(byArray.length);
                    this.out.write(byArray);
                }
                this.debugPrint(element.appendLabel(""));
                this.out.flush();
                break;
            }
            case UTF8_STRING: {
                ASN1UTF8String aSN1UTF8String = ASN1UTF8String.getInstance(aSN1Encodable);
                byte[] byArray = Strings.toUTF8ByteArray(aSN1UTF8String.getString());
                this.encodeLength(byArray.length);
                this.out.write(byArray);
                this.debugPrint(element.appendLabel(""));
                this.out.flush();
                break;
            }
            case BIT_STRING: {
                ASN1BitString aSN1BitString = ASN1BitString.getInstance(aSN1Encodable);
                byte[] byArray = aSN1BitString.getBytes();
                if (element.isFixedLength()) {
                    this.out.write(byArray);
                    this.debugPrint(element.appendLabel(element.rangeExpression()));
                } else {
                    int n = aSN1BitString.getPadBits();
                    this.encodeLength(byArray.length + 1);
                    this.out.write(n);
                    this.out.write(byArray);
                    this.debugPrint(element.appendLabel(element.rangeExpression()));
                }
                this.out.flush();
                break;
            }
            case NULL: {
                break;
            }
            case EXTENSION: {
                ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(aSN1Encodable);
                byte[] byArray = aSN1OctetString.getOctets();
                if (element.isFixedLength()) {
                    this.out.write(byArray);
                } else {
                    this.encodeLength(byArray.length);
                    this.out.write(byArray);
                }
                this.debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                break;
            }
            case ENUM_ITEM: {
                break;
            }
            case BOOLEAN: {
                this.debugPrint(element.getLabel());
                ASN1Boolean aSN1Boolean = ASN1Boolean.getInstance(aSN1Encodable);
                if (aSN1Boolean.isTrue()) {
                    this.out.write(255);
                } else {
                    this.out.write(0);
                }
                this.out.flush();
            }
        }
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

    private void encodeLength(long l) throws IOException {
        if (l <= 127L) {
            this.out.write((int)l);
        } else {
            byte[] byArray = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(l));
            this.out.write(byArray.length | 0x80);
            this.out.write(byArray);
        }
    }

    private void encodeQuantity(long l) throws IOException {
        byte[] byArray = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(l));
        this.out.write(byArray.length);
        this.out.write(byArray);
    }

    @Override
    public void write(int n) throws IOException {
        this.out.write(n);
    }

    public void writePlainType(ASN1Encodable aSN1Encodable, Element element) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OEROutputStream oEROutputStream = new OEROutputStream(byteArrayOutputStream);
        oEROutputStream.write(aSN1Encodable, element);
        oEROutputStream.flush();
        oEROutputStream.close();
        this.encodeLength(byteArrayOutputStream.size());
        this.write(byteArrayOutputStream.toByteArray());
    }
}

