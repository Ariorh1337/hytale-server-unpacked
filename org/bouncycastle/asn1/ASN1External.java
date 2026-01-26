/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERExternal;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLExternal;
import org.bouncycastle.util.Objects;

public abstract class ASN1External
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1External.class, 8){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence.toASN1External();
        }
    };
    ASN1ObjectIdentifier directReference;
    ASN1Integer indirectReference;
    ASN1Primitive dataValueDescriptor;
    int encoding;
    ASN1Primitive externalContent;

    public static ASN1External getInstance(Object object) {
        if (object == null || object instanceof ASN1External) {
            return (ASN1External)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1External) {
                return (ASN1External)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1External)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct external from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1External getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1External)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1External getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1External)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1External(ASN1Sequence aSN1Sequence) {
        int n = 0;
        ASN1Primitive aSN1Primitive = ASN1External.getObjFromSequence(aSN1Sequence, n);
        if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
            this.directReference = (ASN1ObjectIdentifier)aSN1Primitive;
            aSN1Primitive = ASN1External.getObjFromSequence(aSN1Sequence, ++n);
        }
        if (aSN1Primitive instanceof ASN1Integer) {
            this.indirectReference = (ASN1Integer)aSN1Primitive;
            aSN1Primitive = ASN1External.getObjFromSequence(aSN1Sequence, ++n);
        }
        if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
            this.dataValueDescriptor = aSN1Primitive;
            aSN1Primitive = ASN1External.getObjFromSequence(aSN1Sequence, ++n);
        }
        if (aSN1Sequence.size() != n + 1) {
            throw new IllegalArgumentException("input sequence too large");
        }
        if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No tagged object found in sequence. Structure doesn't seem to be of type External");
        }
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
        this.encoding = ASN1External.checkEncoding(aSN1TaggedObject.getTagNo());
        this.externalContent = ASN1External.getExternalContent(aSN1TaggedObject);
    }

    ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, DERTaggedObject dERTaggedObject) {
        this.directReference = aSN1ObjectIdentifier;
        this.indirectReference = aSN1Integer;
        this.dataValueDescriptor = aSN1Primitive;
        this.encoding = ASN1External.checkEncoding(dERTaggedObject.getTagNo());
        this.externalContent = ASN1External.getExternalContent(dERTaggedObject);
    }

    ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, int n, ASN1Primitive aSN1Primitive2) {
        this.directReference = aSN1ObjectIdentifier;
        this.indirectReference = aSN1Integer;
        this.dataValueDescriptor = aSN1Primitive;
        this.encoding = ASN1External.checkEncoding(n);
        this.externalContent = ASN1External.checkExternalContent(n, aSN1Primitive2);
    }

    abstract ASN1Sequence buildSequence();

    @Override
    int encodedLength(boolean bl) throws IOException {
        return this.buildSequence().encodedLength(bl);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeIdentifier(bl, 40);
        this.buildSequence().encode(aSN1OutputStream, false);
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.directReference) ^ Objects.hashCode(this.indirectReference) ^ Objects.hashCode(this.dataValueDescriptor) ^ this.encoding ^ this.externalContent.hashCode();
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (this == aSN1Primitive) {
            return true;
        }
        if (!(aSN1Primitive instanceof ASN1External)) {
            return false;
        }
        ASN1External aSN1External = (ASN1External)aSN1Primitive;
        return Objects.areEqual(this.directReference, aSN1External.directReference) && Objects.areEqual(this.indirectReference, aSN1External.indirectReference) && Objects.areEqual(this.dataValueDescriptor, aSN1External.dataValueDescriptor) && this.encoding == aSN1External.encoding && this.externalContent.equals(aSN1External.externalContent);
    }

    public ASN1Primitive getDataValueDescriptor() {
        return this.dataValueDescriptor;
    }

    public ASN1ObjectIdentifier getDirectReference() {
        return this.directReference;
    }

    public int getEncoding() {
        return this.encoding;
    }

    public ASN1Primitive getExternalContent() {
        return this.externalContent;
    }

    public ASN1Integer getIndirectReference() {
        return this.indirectReference;
    }

    private static int checkEncoding(int n) {
        if (n < 0 || n > 2) {
            throw new IllegalArgumentException("invalid encoding value: " + n);
        }
        return n;
    }

    private static ASN1Primitive checkExternalContent(int n, ASN1Primitive aSN1Primitive) {
        switch (n) {
            case 1: {
                return ASN1OctetString.TYPE.checkedCast(aSN1Primitive);
            }
            case 2: {
                return ASN1BitString.TYPE.checkedCast(aSN1Primitive);
            }
        }
        return aSN1Primitive;
    }

    private static ASN1Primitive getExternalContent(ASN1TaggedObject aSN1TaggedObject) {
        ASN1Util.checkContextTagClass(aSN1TaggedObject);
        switch (aSN1TaggedObject.getTagNo()) {
            case 0: {
                return aSN1TaggedObject.getExplicitBaseObject().toASN1Primitive();
            }
            case 1: {
                return ASN1OctetString.getInstance(aSN1TaggedObject, false);
            }
            case 2: {
                return ASN1BitString.getInstance(aSN1TaggedObject, false);
            }
        }
        throw new IllegalArgumentException("invalid tag: " + ASN1Util.getTagText(aSN1TaggedObject));
    }

    private static ASN1Primitive getObjFromSequence(ASN1Sequence aSN1Sequence, int n) {
        if (aSN1Sequence.size() <= n) {
            throw new IllegalArgumentException("too few objects in input sequence");
        }
        return aSN1Sequence.getObjectAt(n).toASN1Primitive();
    }
}

