/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cms.Attributes;

public class MetaData
extends ASN1Object {
    private ASN1Boolean hashProtected;
    private ASN1UTF8String fileName;
    private ASN1IA5String mediaType;
    private Attributes otherMetaData;

    public MetaData(ASN1Boolean aSN1Boolean, ASN1UTF8String aSN1UTF8String, ASN1IA5String aSN1IA5String, Attributes attributes) {
        this.hashProtected = aSN1Boolean;
        this.fileName = aSN1UTF8String;
        this.mediaType = aSN1IA5String;
        this.otherMetaData = attributes;
    }

    private MetaData(ASN1Sequence aSN1Sequence) {
        this.hashProtected = ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(0));
        int n = 1;
        if (n < aSN1Sequence.size() && aSN1Sequence.getObjectAt(n) instanceof ASN1UTF8String) {
            this.fileName = ASN1UTF8String.getInstance(aSN1Sequence.getObjectAt(n++));
        }
        if (n < aSN1Sequence.size() && aSN1Sequence.getObjectAt(n) instanceof ASN1IA5String) {
            this.mediaType = ASN1IA5String.getInstance(aSN1Sequence.getObjectAt(n++));
        }
        if (n < aSN1Sequence.size()) {
            this.otherMetaData = Attributes.getInstance(aSN1Sequence.getObjectAt(n++));
        }
    }

    public static MetaData getInstance(Object object) {
        if (object instanceof MetaData) {
            return (MetaData)object;
        }
        if (object != null) {
            return new MetaData(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.hashProtected);
        if (this.fileName != null) {
            aSN1EncodableVector.add(this.fileName);
        }
        if (this.mediaType != null) {
            aSN1EncodableVector.add(this.mediaType);
        }
        if (this.otherMetaData != null) {
            aSN1EncodableVector.add(this.otherMetaData);
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public boolean isHashProtected() {
        return this.hashProtected.isTrue();
    }

    public DERUTF8String getFileName() {
        return null == this.fileName || this.fileName instanceof DERUTF8String ? (DERUTF8String)this.fileName : new DERUTF8String(this.fileName.getString());
    }

    public ASN1UTF8String getFileNameUTF8() {
        return this.fileName;
    }

    public DERIA5String getMediaType() {
        return null == this.mediaType || this.mediaType instanceof DERIA5String ? (DERIA5String)this.mediaType : new DERIA5String(this.mediaType.getString(), false);
    }

    public ASN1IA5String getMediaTypeIA5() {
        return this.mediaType;
    }

    public Attributes getOtherMetaData() {
        return this.otherMetaData;
    }
}

