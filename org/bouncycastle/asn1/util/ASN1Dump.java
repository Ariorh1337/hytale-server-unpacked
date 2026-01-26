/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1External;
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
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1T61String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.ASN1VideotexString;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLBitString;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ASN1Dump {
    private static final String TAB = "    ";
    private static final int SAMPLE_SIZE = 32;

    static void _dumpAsString(String string, boolean bl, ASN1Primitive aSN1Primitive, StringBuilder stringBuilder) {
        String string2 = Strings.lineSeparator();
        stringBuilder.append(string);
        if (aSN1Primitive instanceof ASN1Null) {
            stringBuilder.append("NULL");
            stringBuilder.append(string2);
        } else if (aSN1Primitive instanceof ASN1Sequence) {
            if (aSN1Primitive instanceof BERSequence) {
                stringBuilder.append("BER Sequence");
            } else if (aSN1Primitive instanceof DERSequence) {
                stringBuilder.append("DER Sequence");
            } else {
                stringBuilder.append("Sequence");
            }
            stringBuilder.append(string2);
            ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Primitive;
            String string3 = string + TAB;
            int n = aSN1Sequence.size();
            for (int i = 0; i < n; ++i) {
                ASN1Dump._dumpAsString(string3, bl, aSN1Sequence.getObjectAt(i).toASN1Primitive(), stringBuilder);
            }
        } else if (aSN1Primitive instanceof ASN1Set) {
            if (aSN1Primitive instanceof BERSet) {
                stringBuilder.append("BER Set");
            } else if (aSN1Primitive instanceof DERSet) {
                stringBuilder.append("DER Set");
            } else {
                stringBuilder.append("Set");
            }
            stringBuilder.append(string2);
            ASN1Set aSN1Set = (ASN1Set)aSN1Primitive;
            String string4 = string + TAB;
            int n = aSN1Set.size();
            for (int i = 0; i < n; ++i) {
                ASN1Dump._dumpAsString(string4, bl, aSN1Set.getObjectAt(i).toASN1Primitive(), stringBuilder);
            }
        } else if (aSN1Primitive instanceof ASN1TaggedObject) {
            if (aSN1Primitive instanceof BERTaggedObject) {
                stringBuilder.append("BER Tagged ");
            } else if (aSN1Primitive instanceof DERTaggedObject) {
                stringBuilder.append("DER Tagged ");
            } else {
                stringBuilder.append("Tagged ");
            }
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
            stringBuilder.append(ASN1Util.getTagText(aSN1TaggedObject));
            if (!aSN1TaggedObject.isExplicit()) {
                stringBuilder.append(" IMPLICIT");
            }
            stringBuilder.append(string2);
            String string5 = string + TAB;
            ASN1Dump._dumpAsString(string5, bl, aSN1TaggedObject.getBaseObject().toASN1Primitive(), stringBuilder);
        } else if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
            stringBuilder.append("ObjectIdentifier(" + ((ASN1ObjectIdentifier)aSN1Primitive).getId() + ")" + string2);
        } else if (aSN1Primitive instanceof ASN1RelativeOID) {
            stringBuilder.append("RelativeOID(" + ((ASN1RelativeOID)aSN1Primitive).getId() + ")" + string2);
        } else if (aSN1Primitive instanceof ASN1Boolean) {
            stringBuilder.append("Boolean(" + ((ASN1Boolean)aSN1Primitive).isTrue() + ")" + string2);
        } else if (aSN1Primitive instanceof ASN1Integer) {
            stringBuilder.append("Integer(" + ((ASN1Integer)aSN1Primitive).getValue() + ")" + string2);
        } else if (aSN1Primitive instanceof ASN1OctetString) {
            ASN1OctetString aSN1OctetString = (ASN1OctetString)aSN1Primitive;
            if (aSN1Primitive instanceof BEROctetString) {
                stringBuilder.append("BER Constructed Octet String[");
            } else {
                stringBuilder.append("DER Octet String[");
            }
            stringBuilder.append(aSN1OctetString.getOctetsLength() + "]" + string2);
            if (bl) {
                ASN1Dump.dumpBinaryDataAsString(stringBuilder, string, aSN1OctetString.getOctets());
            }
        } else if (aSN1Primitive instanceof ASN1BitString) {
            ASN1BitString aSN1BitString = (ASN1BitString)aSN1Primitive;
            if (aSN1BitString instanceof DERBitString) {
                stringBuilder.append("DER Bit String[");
            } else if (aSN1BitString instanceof DLBitString) {
                stringBuilder.append("DL Bit String[");
            } else {
                stringBuilder.append("BER Bit String[");
            }
            stringBuilder.append(aSN1BitString.getBytesLength() + ", " + aSN1BitString.getPadBits() + "]" + string2);
            if (bl) {
                ASN1Dump.dumpBinaryDataAsString(stringBuilder, string, aSN1BitString.getBytes());
            }
        } else if (aSN1Primitive instanceof ASN1IA5String) {
            stringBuilder.append("IA5String(" + ((ASN1IA5String)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1UTF8String) {
            stringBuilder.append("UTF8String(" + ((ASN1UTF8String)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1NumericString) {
            stringBuilder.append("NumericString(" + ((ASN1NumericString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1PrintableString) {
            stringBuilder.append("PrintableString(" + ((ASN1PrintableString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1VisibleString) {
            stringBuilder.append("VisibleString(" + ((ASN1VisibleString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1BMPString) {
            stringBuilder.append("BMPString(" + ((ASN1BMPString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1T61String) {
            stringBuilder.append("T61String(" + ((ASN1T61String)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1GraphicString) {
            stringBuilder.append("GraphicString(" + ((ASN1GraphicString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1VideotexString) {
            stringBuilder.append("VideotexString(" + ((ASN1VideotexString)aSN1Primitive).getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1UTCTime) {
            stringBuilder.append("UTCTime(" + ((ASN1UTCTime)aSN1Primitive).getTime() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1GeneralizedTime) {
            stringBuilder.append("GeneralizedTime(" + ((ASN1GeneralizedTime)aSN1Primitive).getTime() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1Enumerated) {
            ASN1Enumerated aSN1Enumerated = (ASN1Enumerated)aSN1Primitive;
            stringBuilder.append("DER Enumerated(" + aSN1Enumerated.getValue() + ")" + string2);
        } else if (aSN1Primitive instanceof ASN1ObjectDescriptor) {
            ASN1ObjectDescriptor aSN1ObjectDescriptor = (ASN1ObjectDescriptor)aSN1Primitive;
            stringBuilder.append("ObjectDescriptor(" + aSN1ObjectDescriptor.getBaseGraphicString().getString() + ") " + string2);
        } else if (aSN1Primitive instanceof ASN1External) {
            ASN1External aSN1External = (ASN1External)aSN1Primitive;
            stringBuilder.append("External " + string2);
            String string6 = string + TAB;
            if (aSN1External.getDirectReference() != null) {
                stringBuilder.append(string6 + "Direct Reference: " + aSN1External.getDirectReference().getId() + string2);
            }
            if (aSN1External.getIndirectReference() != null) {
                stringBuilder.append(string6 + "Indirect Reference: " + aSN1External.getIndirectReference().toString() + string2);
            }
            if (aSN1External.getDataValueDescriptor() != null) {
                ASN1Dump._dumpAsString(string6, bl, aSN1External.getDataValueDescriptor(), stringBuilder);
            }
            stringBuilder.append(string6 + "Encoding: " + aSN1External.getEncoding() + string2);
            ASN1Dump._dumpAsString(string6, bl, aSN1External.getExternalContent(), stringBuilder);
        } else {
            stringBuilder.append(aSN1Primitive.toString() + string2);
        }
    }

    public static String dumpAsString(Object object) {
        return ASN1Dump.dumpAsString(object, false);
    }

    public static String dumpAsString(Object object, boolean bl) {
        ASN1Primitive aSN1Primitive;
        if (object instanceof ASN1Primitive) {
            aSN1Primitive = (ASN1Primitive)object;
        } else if (object instanceof ASN1Encodable) {
            aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
        } else {
            return "unknown object type " + object.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        ASN1Dump._dumpAsString("", bl, aSN1Primitive, stringBuilder);
        return stringBuilder.toString();
    }

    private static void dumpBinaryDataAsString(StringBuilder stringBuilder, String string, byte[] byArray) {
        if (byArray.length < 1) {
            return;
        }
        String string2 = Strings.lineSeparator();
        string = string + TAB;
        for (int i = 0; i < byArray.length; i += 32) {
            int n = byArray.length - i;
            int n2 = Math.min(n, 32);
            stringBuilder.append(string);
            stringBuilder.append(Hex.toHexString(byArray, i, n2));
            for (int j = n2; j < 32; ++j) {
                stringBuilder.append("  ");
            }
            stringBuilder.append(TAB);
            ASN1Dump.appendAscString(stringBuilder, byArray, i, n2);
            stringBuilder.append(string2);
        }
    }

    private static void appendAscString(StringBuilder stringBuilder, byte[] byArray, int n, int n2) {
        for (int i = n; i != n + n2; ++i) {
            if (byArray[i] < 32 || byArray[i] > 126) continue;
            stringBuilder.append((char)byArray[i]);
        }
    }
}

