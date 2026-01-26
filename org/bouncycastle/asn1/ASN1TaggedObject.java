/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.ASN1UniversalTypes;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.BERFactory;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLFactory;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.util.Arrays;

public abstract class ASN1TaggedObject
extends ASN1Primitive
implements ASN1TaggedObjectParser {
    private static final int DECLARED_EXPLICIT = 1;
    private static final int DECLARED_IMPLICIT = 2;
    private static final int PARSED_EXPLICIT = 3;
    private static final int PARSED_IMPLICIT = 4;
    final int explicitness;
    final int tagClass;
    final int tagNo;
    final ASN1Encodable obj;

    public static ASN1TaggedObject getInstance(Object object) {
        if (object == null || object instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1TaggedObject) {
                return (ASN1TaggedObject)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return ASN1TaggedObject.checkedCast(ASN1TaggedObject.fromByteArray((byte[])object));
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + object.getClass().getName());
    }

    public static ASN1TaggedObject getInstance(Object object, int n) {
        return ASN1Util.checkTagClass(ASN1TaggedObject.checkInstance(object), n);
    }

    public static ASN1TaggedObject getInstance(Object object, int n, int n2) {
        return ASN1Util.checkTag(ASN1TaggedObject.checkInstance(object), n, n2);
    }

    public static ASN1TaggedObject getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return ASN1Util.getExplicitContextBaseTagged(ASN1TaggedObject.checkInstance(aSN1TaggedObject, bl));
    }

    public static ASN1TaggedObject getInstance(ASN1TaggedObject aSN1TaggedObject, int n, boolean bl) {
        return ASN1Util.getExplicitBaseTagged(ASN1TaggedObject.checkInstance(aSN1TaggedObject, bl), n);
    }

    public static ASN1TaggedObject getInstance(ASN1TaggedObject aSN1TaggedObject, int n, int n2, boolean bl) {
        return ASN1Util.getExplicitBaseTagged(ASN1TaggedObject.checkInstance(aSN1TaggedObject, bl), n, n2);
    }

    public static ASN1TaggedObject getOptional(ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable == null) {
            throw new NullPointerException("'element' cannot be null");
        }
        if (aSN1Encodable instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)aSN1Encodable;
        }
        return null;
    }

    public static ASN1TaggedObject getOptional(ASN1Encodable aSN1Encodable, int n) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getOptional(aSN1Encodable);
        if (aSN1TaggedObject != null && aSN1TaggedObject.hasTagClass(n)) {
            return aSN1TaggedObject;
        }
        return null;
    }

    public static ASN1TaggedObject getOptional(ASN1Encodable aSN1Encodable, int n, int n2) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getOptional(aSN1Encodable);
        if (aSN1TaggedObject != null && aSN1TaggedObject.hasTag(n, n2)) {
            return aSN1TaggedObject;
        }
        return null;
    }

    private static ASN1TaggedObject checkInstance(Object object) {
        if (object == null) {
            throw new NullPointerException("'obj' cannot be null");
        }
        return ASN1TaggedObject.getInstance(object);
    }

    private static ASN1TaggedObject checkInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        if (!bl) {
            throw new IllegalArgumentException("this method not valid for implicitly tagged tagged objects");
        }
        if (aSN1TaggedObject == null) {
            throw new NullPointerException("'taggedObject' cannot be null");
        }
        return aSN1TaggedObject;
    }

    protected ASN1TaggedObject(boolean bl, int n, ASN1Encodable aSN1Encodable) {
        this(bl, 128, n, aSN1Encodable);
    }

    protected ASN1TaggedObject(boolean bl, int n, int n2, ASN1Encodable aSN1Encodable) {
        this(bl ? 1 : 2, n, n2, aSN1Encodable);
    }

    ASN1TaggedObject(int n, int n2, int n3, ASN1Encodable aSN1Encodable) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'obj' cannot be null");
        }
        if (n2 == 0 || (n2 & 0xC0) != n2) {
            throw new IllegalArgumentException("invalid tag class: " + n2);
        }
        this.explicitness = aSN1Encodable instanceof ASN1Choice ? 1 : n;
        this.tagClass = n2;
        this.tagNo = n3;
        this.obj = aSN1Encodable;
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        ASN1Primitive aSN1Primitive2;
        if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
            return false;
        }
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
        if (this.tagNo != aSN1TaggedObject.tagNo || this.tagClass != aSN1TaggedObject.tagClass) {
            return false;
        }
        if (this.explicitness != aSN1TaggedObject.explicitness && this.isExplicit() != aSN1TaggedObject.isExplicit()) {
            return false;
        }
        ASN1Primitive aSN1Primitive3 = this.obj.toASN1Primitive();
        if (aSN1Primitive3 == (aSN1Primitive2 = aSN1TaggedObject.obj.toASN1Primitive())) {
            return true;
        }
        if (!this.isExplicit()) {
            try {
                byte[] byArray = this.getEncoded();
                byte[] byArray2 = aSN1TaggedObject.getEncoded();
                return Arrays.areEqual(byArray, byArray2);
            }
            catch (IOException iOException) {
                return false;
            }
        }
        return aSN1Primitive3.asn1Equals(aSN1Primitive2);
    }

    @Override
    public int hashCode() {
        return this.tagClass * 7919 ^ this.tagNo ^ (this.isExplicit() ? 15 : 240) ^ this.obj.toASN1Primitive().hashCode();
    }

    @Override
    public int getTagClass() {
        return this.tagClass;
    }

    @Override
    public int getTagNo() {
        return this.tagNo;
    }

    @Override
    public boolean hasContextTag() {
        return this.tagClass == 128;
    }

    @Override
    public boolean hasContextTag(int n) {
        return this.tagClass == 128 && this.tagNo == n;
    }

    @Override
    public boolean hasTag(int n, int n2) {
        return this.tagClass == n && this.tagNo == n2;
    }

    @Override
    public boolean hasTagClass(int n) {
        return this.tagClass == n;
    }

    public boolean isExplicit() {
        switch (this.explicitness) {
            case 1: 
            case 3: {
                return true;
            }
        }
        return false;
    }

    boolean isParsed() {
        switch (this.explicitness) {
            case 3: 
            case 4: {
                return true;
            }
        }
        return false;
    }

    public ASN1Object getBaseObject() {
        return this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive();
    }

    public ASN1Object getExplicitBaseObject() {
        if (!this.isExplicit()) {
            throw new IllegalStateException("object implicit - explicit expected.");
        }
        return this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive();
    }

    public ASN1TaggedObject getExplicitBaseTagged() {
        if (!this.isExplicit()) {
            throw new IllegalStateException("object implicit - explicit expected.");
        }
        return ASN1TaggedObject.checkedCast(this.obj.toASN1Primitive());
    }

    public ASN1TaggedObject getImplicitBaseTagged(int n, int n2) {
        if (n == 0 || (n & 0xC0) != n) {
            throw new IllegalArgumentException("invalid base tag class: " + n);
        }
        switch (this.explicitness) {
            case 1: {
                throw new IllegalStateException("object explicit - implicit expected.");
            }
            case 2: {
                ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.checkedCast(this.obj.toASN1Primitive());
                return ASN1Util.checkTag(aSN1TaggedObject, n, n2);
            }
        }
        return this.replaceTag(n, n2);
    }

    public ASN1Primitive getBaseUniversal(boolean bl, int n) {
        ASN1UniversalType aSN1UniversalType = ASN1UniversalTypes.get(n);
        if (null == aSN1UniversalType) {
            throw new IllegalArgumentException("unsupported UNIVERSAL tag number: " + n);
        }
        return this.getBaseUniversal(bl, aSN1UniversalType);
    }

    ASN1Primitive getBaseUniversal(boolean bl, ASN1UniversalType aSN1UniversalType) {
        if (bl) {
            if (!this.isExplicit()) {
                throw new IllegalStateException("object implicit - explicit expected.");
            }
            return aSN1UniversalType.checkedCast(this.obj.toASN1Primitive());
        }
        if (1 == this.explicitness) {
            throw new IllegalStateException("object explicit - implicit expected.");
        }
        ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive();
        switch (this.explicitness) {
            case 3: {
                return aSN1UniversalType.fromImplicitConstructed(this.rebuildConstructed(aSN1Primitive));
            }
            case 4: {
                if (aSN1Primitive instanceof ASN1Sequence) {
                    return aSN1UniversalType.fromImplicitConstructed((ASN1Sequence)aSN1Primitive);
                }
                return aSN1UniversalType.fromImplicitPrimitive((DEROctetString)aSN1Primitive);
            }
        }
        return aSN1UniversalType.checkedCast(aSN1Primitive);
    }

    @Override
    public ASN1Encodable parseBaseUniversal(boolean bl, int n) throws IOException {
        ASN1Primitive aSN1Primitive = this.getBaseUniversal(bl, n);
        switch (n) {
            case 3: {
                return ((ASN1BitString)aSN1Primitive).parser();
            }
            case 4: {
                return ((ASN1OctetString)aSN1Primitive).parser();
            }
            case 16: {
                return ((ASN1Sequence)aSN1Primitive).parser();
            }
            case 17: {
                return ((ASN1Set)aSN1Primitive).parser();
            }
        }
        return aSN1Primitive;
    }

    @Override
    public ASN1Encodable parseExplicitBaseObject() throws IOException {
        return this.getExplicitBaseObject();
    }

    @Override
    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
        return this.getExplicitBaseTagged();
    }

    @Override
    public ASN1TaggedObjectParser parseImplicitBaseTagged(int n, int n2) throws IOException {
        return this.getImplicitBaseTagged(n, n2);
    }

    @Override
    public final ASN1Primitive getLoadedObject() {
        return this;
    }

    abstract ASN1Sequence rebuildConstructed(ASN1Primitive var1);

    abstract ASN1TaggedObject replaceTag(int var1, int var2);

    @Override
    ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
    }

    public String toString() {
        return ASN1Util.getTagText(this.tagClass, this.tagNo) + this.obj;
    }

    static ASN1Primitive createConstructedDL(int n, int n2, ASN1EncodableVector aSN1EncodableVector) {
        boolean bl = aSN1EncodableVector.size() == 1;
        return bl ? new DLTaggedObject(3, n, n2, aSN1EncodableVector.get(0)) : new DLTaggedObject(4, n, n2, (ASN1Encodable)DLFactory.createSequence(aSN1EncodableVector));
    }

    static ASN1Primitive createConstructedIL(int n, int n2, ASN1EncodableVector aSN1EncodableVector) {
        boolean bl = aSN1EncodableVector.size() == 1;
        return bl ? new BERTaggedObject(3, n, n2, aSN1EncodableVector.get(0)) : new BERTaggedObject(4, n, n2, (ASN1Encodable)BERFactory.createSequence(aSN1EncodableVector));
    }

    static ASN1Primitive createPrimitive(int n, int n2, byte[] byArray) {
        return new DLTaggedObject(4, n, n2, (ASN1Encodable)new DEROctetString(byArray));
    }

    private static ASN1TaggedObject checkedCast(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)aSN1Primitive;
        }
        throw new IllegalStateException("unexpected object: " + aSN1Primitive.getClass().getName());
    }
}

