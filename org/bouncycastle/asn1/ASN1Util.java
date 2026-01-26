/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Tag;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;

public abstract class ASN1Util {
    static ASN1TaggedObject checkContextTag(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.checkTag(aSN1TaggedObject, 128, n);
    }

    static ASN1TaggedObjectParser checkContextTag(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) {
        return ASN1Util.checkTag(aSN1TaggedObjectParser, 128, n);
    }

    static ASN1TaggedObject checkContextTagClass(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.checkTagClass(aSN1TaggedObject, 128);
    }

    static ASN1TaggedObjectParser checkContextTagClass(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return ASN1Util.checkTagClass(aSN1TaggedObjectParser, 128);
    }

    static ASN1TaggedObject checkTag(ASN1TaggedObject aSN1TaggedObject, int n, int n2) {
        if (!aSN1TaggedObject.hasTag(n, n2)) {
            String string = ASN1Util.getTagText(n, n2);
            String string2 = ASN1Util.getTagText(aSN1TaggedObject);
            throw new IllegalStateException("Expected " + string + " tag but found " + string2);
        }
        return aSN1TaggedObject;
    }

    static ASN1TaggedObjectParser checkTag(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2) {
        if (!aSN1TaggedObjectParser.hasTag(n, n2)) {
            String string = ASN1Util.getTagText(n, n2);
            String string2 = ASN1Util.getTagText(aSN1TaggedObjectParser);
            throw new IllegalStateException("Expected " + string + " tag but found " + string2);
        }
        return aSN1TaggedObjectParser;
    }

    static ASN1TaggedObject checkTagClass(ASN1TaggedObject aSN1TaggedObject, int n) {
        if (!aSN1TaggedObject.hasTagClass(n)) {
            String string = ASN1Util.getTagClassText(n);
            String string2 = ASN1Util.getTagClassText(aSN1TaggedObject);
            throw new IllegalStateException("Expected " + string + " tag but found " + string2);
        }
        return aSN1TaggedObject;
    }

    static ASN1TaggedObjectParser checkTagClass(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) {
        if (!aSN1TaggedObjectParser.hasTagClass(n)) {
            String string = ASN1Util.getTagClassText(n);
            String string2 = ASN1Util.getTagClassText(aSN1TaggedObjectParser);
            throw new IllegalStateException("Expected " + string + " tag but found " + string2);
        }
        return aSN1TaggedObjectParser;
    }

    public static Object getInstanceChoiceBaseObject(ASN1TaggedObject aSN1TaggedObject, boolean bl, String string) {
        if (!bl) {
            String string2 = "Implicit tagging cannot be used with untagged choice type " + string + " (X.680 30.6, 30.8).";
            throw new IllegalArgumentException(string2);
        }
        if (aSN1TaggedObject == null) {
            throw new NullPointerException("'taggedObject' cannot be null");
        }
        return ASN1Util.getExplicitContextBaseObject(aSN1TaggedObject);
    }

    public static Object getTaggedChoiceBaseObject(ASN1TaggedObject aSN1TaggedObject, boolean bl, String string) {
        if (!bl) {
            String string2 = "Implicit tagging cannot be used with untagged choice type " + string + " (X.680 30.6, 30.8).";
            throw new IllegalArgumentException(string2);
        }
        if (aSN1TaggedObject == null) {
            throw new NullPointerException("'taggedObject' cannot be null");
        }
        return aSN1TaggedObject.getExplicitBaseObject();
    }

    static String getTagClassText(ASN1Tag aSN1Tag) {
        return ASN1Util.getTagClassText(aSN1Tag.getTagClass());
    }

    public static String getTagClassText(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.getTagClassText(aSN1TaggedObject.getTagClass());
    }

    public static String getTagClassText(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return ASN1Util.getTagClassText(aSN1TaggedObjectParser.getTagClass());
    }

    public static String getTagClassText(int n) {
        switch (n) {
            case 64: {
                return "APPLICATION";
            }
            case 128: {
                return "CONTEXT";
            }
            case 192: {
                return "PRIVATE";
            }
        }
        return "UNIVERSAL";
    }

    static String getTagText(ASN1Tag aSN1Tag) {
        return ASN1Util.getTagText(aSN1Tag.getTagClass(), aSN1Tag.getTagNumber());
    }

    public static String getTagText(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.getTagText(aSN1TaggedObject.getTagClass(), aSN1TaggedObject.getTagNo());
    }

    public static String getTagText(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return ASN1Util.getTagText(aSN1TaggedObjectParser.getTagClass(), aSN1TaggedObjectParser.getTagNo());
    }

    public static String getTagText(int n, int n2) {
        switch (n) {
            case 64: {
                return "[APPLICATION " + n2 + "]";
            }
            case 128: {
                return "[CONTEXT " + n2 + "]";
            }
            case 192: {
                return "[PRIVATE " + n2 + "]";
            }
        }
        return "[UNIVERSAL " + n2 + "]";
    }

    public static ASN1Object getExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.checkTagClass(aSN1TaggedObject, n).getExplicitBaseObject();
    }

    public static ASN1Object getExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int n, int n2) {
        return ASN1Util.checkTag(aSN1TaggedObject, n, n2).getExplicitBaseObject();
    }

    public static ASN1Object getExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.getExplicitBaseObject(aSN1TaggedObject, 128);
    }

    public static ASN1Object getExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.getExplicitBaseObject(aSN1TaggedObject, 128, n);
    }

    public static ASN1Object tryGetExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int n) {
        if (!aSN1TaggedObject.hasTagClass(n)) {
            return null;
        }
        return aSN1TaggedObject.getExplicitBaseObject();
    }

    public static ASN1Object tryGetExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int n, int n2) {
        if (!aSN1TaggedObject.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObject.getExplicitBaseObject();
    }

    public static ASN1Object tryGetExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.tryGetExplicitBaseObject(aSN1TaggedObject, 128);
    }

    public static ASN1Object tryGetExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.tryGetExplicitBaseObject(aSN1TaggedObject, 128, n);
    }

    public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.checkTagClass(aSN1TaggedObject, n).getExplicitBaseTagged();
    }

    public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2) {
        return ASN1Util.checkTag(aSN1TaggedObject, n, n2).getExplicitBaseTagged();
    }

    public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.getExplicitBaseTagged(aSN1TaggedObject, 128);
    }

    public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.getExplicitBaseTagged(aSN1TaggedObject, 128, n);
    }

    public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n) {
        if (!aSN1TaggedObject.hasTagClass(n)) {
            return null;
        }
        return aSN1TaggedObject.getExplicitBaseTagged();
    }

    public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2) {
        if (!aSN1TaggedObject.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObject.getExplicitBaseTagged();
    }

    public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject) {
        return ASN1Util.tryGetExplicitBaseTagged(aSN1TaggedObject, 128);
    }

    public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n) {
        return ASN1Util.tryGetExplicitBaseTagged(aSN1TaggedObject, 128, n);
    }

    public static ASN1TaggedObject getImplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2, int n3, int n4) {
        return ASN1Util.checkTag(aSN1TaggedObject, n, n2).getImplicitBaseTagged(n3, n4);
    }

    public static ASN1TaggedObject getImplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2, int n3) {
        return ASN1Util.getImplicitBaseTagged(aSN1TaggedObject, 128, n, n2, n3);
    }

    public static ASN1TaggedObject tryGetImplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2, int n3, int n4) {
        if (!aSN1TaggedObject.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObject.getImplicitBaseTagged(n3, n4);
    }

    public static ASN1TaggedObject tryGetImplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int n, int n2, int n3) {
        return ASN1Util.tryGetImplicitBaseTagged(aSN1TaggedObject, 128, n, n2, n3);
    }

    public static ASN1Primitive getBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int n, int n2, boolean bl, int n3) {
        return ASN1Util.checkTag(aSN1TaggedObject, n, n2).getBaseUniversal(bl, n3);
    }

    public static ASN1Primitive getContextBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int n, boolean bl, int n2) {
        return ASN1Util.getBaseUniversal(aSN1TaggedObject, 128, n, bl, n2);
    }

    public static ASN1Primitive tryGetBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int n, int n2, boolean bl, int n3) {
        if (!aSN1TaggedObject.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObject.getBaseUniversal(bl, n3);
    }

    public static ASN1Primitive tryGetContextBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int n, boolean bl, int n2) {
        return ASN1Util.tryGetBaseUniversal(aSN1TaggedObject, 128, n, bl, n2);
    }

    public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        return ASN1Util.checkTagClass(aSN1TaggedObjectParser, n).parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2) throws IOException {
        return ASN1Util.checkTag(aSN1TaggedObjectParser, n, n2).parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser) throws IOException {
        return ASN1Util.parseExplicitBaseTagged(aSN1TaggedObjectParser, 128);
    }

    public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        return ASN1Util.parseExplicitBaseTagged(aSN1TaggedObjectParser, 128, n);
    }

    public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        if (!aSN1TaggedObjectParser.hasTagClass(n)) {
            return null;
        }
        return aSN1TaggedObjectParser.parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2) throws IOException {
        if (!aSN1TaggedObjectParser.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObjectParser.parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser) throws IOException {
        return ASN1Util.tryParseExplicitBaseTagged(aSN1TaggedObjectParser, 128);
    }

    public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        return ASN1Util.tryParseExplicitBaseTagged(aSN1TaggedObjectParser, 128, n);
    }

    public static ASN1TaggedObjectParser parseImplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, int n3, int n4) throws IOException {
        return ASN1Util.checkTag(aSN1TaggedObjectParser, n, n2).parseImplicitBaseTagged(n3, n4);
    }

    public static ASN1TaggedObjectParser parseImplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, int n3) throws IOException {
        return ASN1Util.parseImplicitBaseTagged(aSN1TaggedObjectParser, 128, n, n2, n3);
    }

    public static ASN1TaggedObjectParser tryParseImplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, int n3, int n4) throws IOException {
        if (!aSN1TaggedObjectParser.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObjectParser.parseImplicitBaseTagged(n3, n4);
    }

    public static ASN1TaggedObjectParser tryParseImplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, int n3) throws IOException {
        return ASN1Util.tryParseImplicitBaseTagged(aSN1TaggedObjectParser, 128, n, n2, n3);
    }

    public static ASN1Encodable parseBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, boolean bl, int n3) throws IOException {
        return ASN1Util.checkTag(aSN1TaggedObjectParser, n, n2).parseBaseUniversal(bl, n3);
    }

    public static ASN1Encodable parseContextBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, boolean bl, int n2) throws IOException {
        return ASN1Util.parseBaseUniversal(aSN1TaggedObjectParser, 128, n, bl, n2);
    }

    public static ASN1Encodable tryParseBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2, boolean bl, int n3) throws IOException {
        if (!aSN1TaggedObjectParser.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObjectParser.parseBaseUniversal(bl, n3);
    }

    public static ASN1Encodable tryParseContextBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, boolean bl, int n2) throws IOException {
        return ASN1Util.tryParseBaseUniversal(aSN1TaggedObjectParser, 128, n, bl, n2);
    }

    public static ASN1Encodable parseExplicitBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2) throws IOException {
        return ASN1Util.checkTag(aSN1TaggedObjectParser, n, n2).parseExplicitBaseObject();
    }

    public static ASN1Encodable parseExplicitContextBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        return ASN1Util.parseExplicitBaseObject(aSN1TaggedObjectParser, 128, n);
    }

    public static ASN1Encodable tryParseExplicitBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n, int n2) throws IOException {
        if (!aSN1TaggedObjectParser.hasTag(n, n2)) {
            return null;
        }
        return aSN1TaggedObjectParser.parseExplicitBaseObject();
    }

    public static ASN1Encodable tryParseExplicitContextBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int n) throws IOException {
        return ASN1Util.tryParseExplicitBaseObject(aSN1TaggedObjectParser, 128, n);
    }
}

