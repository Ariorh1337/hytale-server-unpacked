/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.ContributedExtensionBlocks;
import org.bouncycastle.oer.its.ieee1609dot2.MissingCrlIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.PduFunctionalType;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ThreeDLocation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;

public class HeaderInfo
extends ASN1Object {
    private final Psid psid;
    private final Time64 generationTime;
    private final Time64 expiryTime;
    private final ThreeDLocation generationLocation;
    private final HashedId3 p2pcdLearningRequest;
    private final MissingCrlIdentifier missingCrlIdentifier;
    private final EncryptionKey encryptionKey;
    private final SequenceOfHashedId3 inlineP2pcdRequest;
    private final Certificate requestedCertificate;
    private final PduFunctionalType pduFunctionalType;
    private final ContributedExtensionBlocks contributedExtensions;

    private HeaderInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 11 && aSN1Sequence.size() != 7) {
            throw new IllegalArgumentException("expected sequence size of 11 or 7");
        }
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        this.psid = Psid.getInstance(iterator.next());
        this.generationTime = OEROptional.getValue(Time64.class, iterator.next());
        this.expiryTime = OEROptional.getValue(Time64.class, iterator.next());
        this.generationLocation = OEROptional.getValue(ThreeDLocation.class, iterator.next());
        this.p2pcdLearningRequest = OEROptional.getValue(HashedId3.class, iterator.next());
        this.missingCrlIdentifier = OEROptional.getValue(MissingCrlIdentifier.class, iterator.next());
        this.encryptionKey = OEROptional.getValue(EncryptionKey.class, iterator.next());
        if (aSN1Sequence.size() > 7) {
            this.inlineP2pcdRequest = OEROptional.getValue(SequenceOfHashedId3.class, iterator.next());
            this.requestedCertificate = OEROptional.getValue(Certificate.class, iterator.next());
            this.pduFunctionalType = OEROptional.getValue(PduFunctionalType.class, iterator.next());
            this.contributedExtensions = OEROptional.getValue(ContributedExtensionBlocks.class, iterator.next());
        } else {
            this.inlineP2pcdRequest = null;
            this.requestedCertificate = null;
            this.pduFunctionalType = null;
            this.contributedExtensions = null;
        }
    }

    public HeaderInfo(Psid psid, Time64 time64, Time64 time642, ThreeDLocation threeDLocation, HashedId3 hashedId3, MissingCrlIdentifier missingCrlIdentifier, EncryptionKey encryptionKey, SequenceOfHashedId3 sequenceOfHashedId3, Certificate certificate, PduFunctionalType pduFunctionalType, ContributedExtensionBlocks contributedExtensionBlocks) {
        this.psid = psid;
        this.generationTime = time64;
        this.expiryTime = time642;
        this.generationLocation = threeDLocation;
        this.p2pcdLearningRequest = hashedId3;
        this.missingCrlIdentifier = missingCrlIdentifier;
        this.encryptionKey = encryptionKey;
        this.inlineP2pcdRequest = sequenceOfHashedId3;
        this.requestedCertificate = certificate;
        this.pduFunctionalType = pduFunctionalType;
        this.contributedExtensions = contributedExtensionBlocks;
    }

    public static HeaderInfo getInstance(Object object) {
        if (object instanceof HeaderInfo) {
            return (HeaderInfo)object;
        }
        if (object != null) {
            return new HeaderInfo(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Psid getPsid() {
        return this.psid;
    }

    public Time64 getGenerationTime() {
        return this.generationTime;
    }

    public Time64 getExpiryTime() {
        return this.expiryTime;
    }

    public ThreeDLocation getGenerationLocation() {
        return this.generationLocation;
    }

    public HashedId3 getP2pcdLearningRequest() {
        return this.p2pcdLearningRequest;
    }

    public MissingCrlIdentifier getMissingCrlIdentifier() {
        return this.missingCrlIdentifier;
    }

    public EncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public SequenceOfHashedId3 getInlineP2pcdRequest() {
        return this.inlineP2pcdRequest;
    }

    public Certificate getRequestedCertificate() {
        return this.requestedCertificate;
    }

    public PduFunctionalType getPduFunctionalType() {
        return this.pduFunctionalType;
    }

    public ContributedExtensionBlocks getContributedExtensions() {
        return this.contributedExtensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance(this.generationTime), OEROptional.getInstance(this.expiryTime), OEROptional.getInstance(this.generationLocation), OEROptional.getInstance(this.p2pcdLearningRequest), OEROptional.getInstance(this.missingCrlIdentifier), OEROptional.getInstance(this.encryptionKey), OEROptional.getInstance(this.inlineP2pcdRequest), OEROptional.getInstance(this.requestedCertificate), OEROptional.getInstance(this.pduFunctionalType), OEROptional.getInstance(this.contributedExtensions)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Psid psid;
        private Time64 generationTime;
        private Time64 expiryTime;
        private ThreeDLocation generationLocation;
        private HashedId3 p2pcdLearningRequest;
        private MissingCrlIdentifier missingCrlIdentifier;
        private EncryptionKey encryptionKey;
        private SequenceOfHashedId3 inlineP2pcdRequest;
        private Certificate requestedCertificate;
        private PduFunctionalType pduFunctionalType;
        private ContributedExtensionBlocks contributedExtensions;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setGenerationTime(Time64 time64) {
            this.generationTime = time64;
            return this;
        }

        public Builder setExpiryTime(Time64 time64) {
            this.expiryTime = time64;
            return this;
        }

        public Builder setGenerationLocation(ThreeDLocation threeDLocation) {
            this.generationLocation = threeDLocation;
            return this;
        }

        public Builder setP2pcdLearningRequest(HashedId3 hashedId3) {
            this.p2pcdLearningRequest = hashedId3;
            return this;
        }

        public Builder setEncryptionKey(EncryptionKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setMissingCrlIdentifier(MissingCrlIdentifier missingCrlIdentifier) {
            this.missingCrlIdentifier = missingCrlIdentifier;
            return this;
        }

        public Builder setInlineP2pcdRequest(SequenceOfHashedId3 sequenceOfHashedId3) {
            this.inlineP2pcdRequest = sequenceOfHashedId3;
            return this;
        }

        public Builder setRequestedCertificate(Certificate certificate) {
            this.requestedCertificate = certificate;
            return this;
        }

        public Builder setPduFunctionalType(PduFunctionalType pduFunctionalType) {
            this.pduFunctionalType = pduFunctionalType;
            return this;
        }

        public Builder setContributedExtensions(ContributedExtensionBlocks contributedExtensionBlocks) {
            this.contributedExtensions = contributedExtensionBlocks;
            return this;
        }

        public HeaderInfo createHeaderInfo() {
            return new HeaderInfo(this.psid, this.generationTime, this.expiryTime, this.generationLocation, this.p2pcdLearningRequest, this.missingCrlIdentifier, this.encryptionKey, this.inlineP2pcdRequest, this.requestedCertificate, this.pduFunctionalType, this.contributedExtensions);
        }
    }
}

