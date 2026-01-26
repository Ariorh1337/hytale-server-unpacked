/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.util.Arrays;

public class CVCertificateRequest
extends ASN1Object {
    private final ASN1TaggedObject original;
    private CertificateBody certificateBody;
    private byte[] innerSignature = null;
    private byte[] outerSignature = null;
    private static final int bodyValid = 1;
    private static final int signValid = 2;

    private CVCertificateRequest(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.original = aSN1TaggedObject;
        if (aSN1TaggedObject.hasTag(64, 7)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16));
            this.initCertBody(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0), 64));
            this.outerSignature = ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(aSN1Sequence.size() - 1)).getBaseUniversal(false, 4)).getOctets();
        } else {
            this.initCertBody(aSN1TaggedObject);
        }
    }

    private void initCertBody(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        if (aSN1TaggedObject.hasTag(64, 33)) {
            int n = 0;
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16));
            Enumeration enumeration = aSN1Sequence.getObjects();
            block4: while (enumeration.hasMoreElements()) {
                ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(enumeration.nextElement(), 64);
                switch (aSN1TaggedObject2.getTagNo()) {
                    case 78: {
                        this.certificateBody = CertificateBody.getInstance(aSN1TaggedObject2);
                        n |= 1;
                        continue block4;
                    }
                    case 55: {
                        this.innerSignature = ASN1OctetString.getInstance(aSN1TaggedObject2.getBaseUniversal(false, 4)).getOctets();
                        n |= 2;
                        continue block4;
                    }
                }
                throw new IOException("Invalid tag, not an CV Certificate Request element:" + aSN1TaggedObject2.getTagNo());
            }
            if ((n & 3) == 0) {
                throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + aSN1TaggedObject.getTagNo());
            }
        } else {
            throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + aSN1TaggedObject.getTagNo());
        }
    }

    public static CVCertificateRequest getInstance(Object object) {
        if (object instanceof CVCertificateRequest) {
            return (CVCertificateRequest)object;
        }
        if (object != null) {
            try {
                return new CVCertificateRequest(ASN1TaggedObject.getInstance(object, 64));
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("unable to parse data: " + iOException.getMessage(), iOException);
            }
        }
        return null;
    }

    public CertificateBody getCertificateBody() {
        return this.certificateBody;
    }

    public PublicKeyDataObject getPublicKey() {
        return this.certificateBody.getPublicKey();
    }

    public byte[] getInnerSignature() {
        return Arrays.clone(this.innerSignature);
    }

    public byte[] getOuterSignature() {
        return Arrays.clone(this.outerSignature);
    }

    public boolean hasOuterSignature() {
        return this.outerSignature != null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.original != null) {
            return this.original;
        }
        DERSequence dERSequence = new DERSequence(this.certificateBody, EACTagged.create(55, this.innerSignature));
        return EACTagged.create(33, dERSequence);
    }
}

