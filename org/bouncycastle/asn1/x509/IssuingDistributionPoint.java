/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.util.Strings;

public class IssuingDistributionPoint
extends ASN1Object {
    private DistributionPointName distributionPoint;
    private boolean onlyContainsUserCerts;
    private boolean onlyContainsCACerts;
    private ReasonFlags onlySomeReasons;
    private boolean indirectCRL;
    private boolean onlyContainsAttributeCerts;
    private ASN1Sequence seq;

    public static IssuingDistributionPoint getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return IssuingDistributionPoint.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static IssuingDistributionPoint getInstance(Object object) {
        if (object instanceof IssuingDistributionPoint) {
            return (IssuingDistributionPoint)object;
        }
        if (object != null) {
            return new IssuingDistributionPoint(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public IssuingDistributionPoint(DistributionPointName distributionPointName, boolean bl, boolean bl2, ReasonFlags reasonFlags, boolean bl3, boolean bl4) {
        if (bl2 && (bl || bl4) || bl && bl4) {
            throw new IllegalArgumentException("only one of onlyContainsCACerts, onlyContainsUserCerts, or onlyContainsAttributeCerts can be true");
        }
        this.distributionPoint = distributionPointName;
        this.indirectCRL = bl3;
        this.onlyContainsAttributeCerts = bl4;
        this.onlyContainsCACerts = bl2;
        this.onlyContainsUserCerts = bl;
        this.onlySomeReasons = reasonFlags;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        if (distributionPointName != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable)distributionPointName));
        }
        if (bl) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable)ASN1Boolean.getInstance(true)));
        }
        if (bl2) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable)ASN1Boolean.getInstance(true)));
        }
        if (reasonFlags != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 3, (ASN1Encodable)reasonFlags));
        }
        if (bl3) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 4, (ASN1Encodable)ASN1Boolean.getInstance(true)));
        }
        if (bl4) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 5, (ASN1Encodable)ASN1Boolean.getInstance(true)));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public IssuingDistributionPoint(DistributionPointName distributionPointName, boolean bl, boolean bl2) {
        this(distributionPointName, false, false, null, bl, bl2);
    }

    private IssuingDistributionPoint(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
        block8: for (int i = 0; i != aSN1Sequence.size(); ++i) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(i));
            switch (aSN1TaggedObject.getTagNo()) {
                case 0: {
                    this.distributionPoint = DistributionPointName.getInstance(aSN1TaggedObject, true);
                    continue block8;
                }
                case 1: {
                    this.onlyContainsUserCerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
                    continue block8;
                }
                case 2: {
                    this.onlyContainsCACerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
                    continue block8;
                }
                case 3: {
                    this.onlySomeReasons = new ReasonFlags(ASN1BitString.getInstance(aSN1TaggedObject, false));
                    continue block8;
                }
                case 4: {
                    this.indirectCRL = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
                    continue block8;
                }
                case 5: {
                    this.onlyContainsAttributeCerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
                    continue block8;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag in IssuingDistributionPoint");
                }
            }
        }
    }

    public boolean onlyContainsUserCerts() {
        return this.onlyContainsUserCerts;
    }

    public boolean onlyContainsCACerts() {
        return this.onlyContainsCACerts;
    }

    public boolean isIndirectCRL() {
        return this.indirectCRL;
    }

    public boolean onlyContainsAttributeCerts() {
        return this.onlyContainsAttributeCerts;
    }

    public DistributionPointName getDistributionPoint() {
        return this.distributionPoint;
    }

    public ReasonFlags getOnlySomeReasons() {
        return this.onlySomeReasons;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }

    public String toString() {
        String string = Strings.lineSeparator();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IssuingDistributionPoint: [");
        stringBuilder.append(string);
        if (this.distributionPoint != null) {
            this.appendObject(stringBuilder, string, "distributionPoint", this.distributionPoint.toString());
        }
        if (this.onlyContainsUserCerts) {
            this.appendObject(stringBuilder, string, "onlyContainsUserCerts", this.booleanToString(this.onlyContainsUserCerts));
        }
        if (this.onlyContainsCACerts) {
            this.appendObject(stringBuilder, string, "onlyContainsCACerts", this.booleanToString(this.onlyContainsCACerts));
        }
        if (this.onlySomeReasons != null) {
            this.appendObject(stringBuilder, string, "onlySomeReasons", this.onlySomeReasons.toString());
        }
        if (this.onlyContainsAttributeCerts) {
            this.appendObject(stringBuilder, string, "onlyContainsAttributeCerts", this.booleanToString(this.onlyContainsAttributeCerts));
        }
        if (this.indirectCRL) {
            this.appendObject(stringBuilder, string, "indirectCRL", this.booleanToString(this.indirectCRL));
        }
        stringBuilder.append("]");
        stringBuilder.append(string);
        return stringBuilder.toString();
    }

    private void appendObject(StringBuilder stringBuilder, String string, String string2, String string3) {
        String string4 = "    ";
        stringBuilder.append(string4);
        stringBuilder.append(string2);
        stringBuilder.append(":");
        stringBuilder.append(string);
        stringBuilder.append(string4);
        stringBuilder.append(string4);
        stringBuilder.append(string3);
        stringBuilder.append(string);
    }

    private String booleanToString(boolean bl) {
        return bl ? "true" : "false";
    }
}

