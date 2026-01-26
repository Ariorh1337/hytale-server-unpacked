/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AuthenticatedData
extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private AlgorithmIdentifier macAlgorithm;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthenticatedData(OriginatorInfo originatorInfo, ASN1Set aSN1Set, AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, ContentInfo contentInfo, ASN1Set aSN1Set2, ASN1OctetString aSN1OctetString, ASN1Set aSN1Set3) {
        if (!(algorithmIdentifier2 == null && aSN1Set2 == null || algorithmIdentifier2 != null && aSN1Set2 != null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer(AuthenticatedData.calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = algorithmIdentifier;
        this.digestAlgorithm = algorithmIdentifier2;
        this.recipientInfos = aSN1Set;
        this.encapsulatedContentInfo = contentInfo;
        this.authAttrs = aSN1Set2;
        this.mac = aSN1OctetString;
        this.unauthAttrs = aSN1Set3;
    }

    private AuthenticatedData(ASN1Sequence aSN1Sequence) {
        int n = 0;
        this.version = (ASN1Integer)aSN1Sequence.getObjectAt(n++);
        ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        if (aSN1Encodable instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)aSN1Encodable, false);
            aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        }
        this.recipientInfos = ASN1Set.getInstance(aSN1Encodable);
        this.macAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(n++));
        if ((aSN1Encodable = aSN1Sequence.getObjectAt(n++)) instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)aSN1Encodable, false);
            aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(aSN1Encodable);
        if ((aSN1Encodable = aSN1Sequence.getObjectAt(n++)) instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)aSN1Encodable, false);
            aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        }
        this.mac = ASN1OctetString.getInstance(aSN1Encodable);
        if (aSN1Sequence.size() > n) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(n), false);
        }
    }

    public static AuthenticatedData getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return AuthenticatedData.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static AuthenticatedData getInstance(Object object) {
        if (object instanceof AuthenticatedData) {
            return (AuthenticatedData)object;
        }
        if (object != null) {
            return new AuthenticatedData(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
    }

    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }

    public ASN1OctetString getMac() {
        return this.mac;
    }

    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(9);
        aSN1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        aSN1EncodableVector.add(this.recipientInfos);
        aSN1EncodableVector.add(this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.digestAlgorithm));
        }
        aSN1EncodableVector.add(this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.authAttrs));
        }
        aSN1EncodableVector.add(this.mac);
        if (this.unauthAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 3, (ASN1Encodable)this.unauthAttrs));
        }
        return new BERSequence(aSN1EncodableVector);
    }

    public static int calculateVersion(OriginatorInfo originatorInfo) {
        if (originatorInfo != null) {
            ASN1Set aSN1Set;
            int n;
            ASN1Set aSN1Set2 = originatorInfo.getCRLs();
            if (aSN1Set2 != null) {
                n = aSN1Set2.size();
                for (int i = 0; i < n; ++i) {
                    ASN1TaggedObject aSN1TaggedObject;
                    ASN1Encodable aSN1Encodable = aSN1Set2.getObjectAt(i);
                    if (!(aSN1Encodable instanceof ASN1TaggedObject) || !(aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable).hasContextTag(1)) continue;
                    return 3;
                }
            }
            if ((aSN1Set = originatorInfo.getCertificates()) != null) {
                n = 0;
                int n2 = aSN1Set.size();
                for (int i = 0; i < n2; ++i) {
                    ASN1Encodable aSN1Encodable = aSN1Set.getObjectAt(i);
                    if (!(aSN1Encodable instanceof ASN1TaggedObject)) continue;
                    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable;
                    if (aSN1TaggedObject.hasContextTag(3)) {
                        return 3;
                    }
                    n = n != 0 || aSN1TaggedObject.hasContextTag(2) ? 1 : 0;
                }
                if (n != 0) {
                    return 1;
                }
            }
        }
        return 0;
    }
}

