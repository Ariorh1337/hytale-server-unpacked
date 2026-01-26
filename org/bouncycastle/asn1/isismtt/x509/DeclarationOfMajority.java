/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class DeclarationOfMajority
extends ASN1Object
implements ASN1Choice {
    public static final int notYoungerThan = 0;
    public static final int fullAgeAtCountry = 1;
    public static final int dateOfBirth = 2;
    private ASN1TaggedObject declaration;

    public DeclarationOfMajority(int n) {
        this.declaration = new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(n));
    }

    public DeclarationOfMajority(boolean bl, String string) {
        if (string.length() > 2) {
            throw new IllegalArgumentException("country can only be 2 characters");
        }
        this.declaration = bl ? new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence(new DERPrintableString(string, true))) : new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence(ASN1Boolean.FALSE, new DERPrintableString(string, true)));
    }

    public DeclarationOfMajority(ASN1GeneralizedTime aSN1GeneralizedTime) {
        this.declaration = new DERTaggedObject(false, 2, (ASN1Encodable)aSN1GeneralizedTime);
    }

    public static DeclarationOfMajority getInstance(Object object) {
        if (object == null || object instanceof DeclarationOfMajority) {
            return (DeclarationOfMajority)object;
        }
        if (object instanceof ASN1TaggedObject) {
            return new DeclarationOfMajority(ASN1TaggedObject.getInstance(object, 128));
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    private DeclarationOfMajority(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.getTagNo() > 2) {
            throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject.getTagNo());
        }
        this.declaration = aSN1TaggedObject;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.declaration;
    }

    public int getType() {
        return this.declaration.getTagNo();
    }

    public int notYoungerThan() {
        if (this.declaration.getTagNo() != 0) {
            return -1;
        }
        return ASN1Integer.getInstance(this.declaration, false).intValueExact();
    }

    public ASN1Sequence fullAgeAtCountry() {
        if (this.declaration.getTagNo() != 1) {
            return null;
        }
        return ASN1Sequence.getInstance(this.declaration, false);
    }

    public ASN1GeneralizedTime getDateOfBirth() {
        if (this.declaration.getTagNo() != 2) {
            return null;
        }
        return ASN1GeneralizedTime.getInstance(this.declaration, false);
    }
}

