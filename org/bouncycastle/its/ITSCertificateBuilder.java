/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSValidityPeriod;
import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ITSCertificateBuilder {
    protected final ToBeSignedCertificate.Builder tbsCertificateBuilder;
    protected final ITSCertificate issuer;
    protected UINT8 version = new UINT8(3);
    protected HashedId3 cracaId = new HashedId3(new byte[3]);
    protected CrlSeries crlSeries = new CrlSeries(0);

    public ITSCertificateBuilder(ToBeSignedCertificate.Builder builder) {
        this(null, builder);
    }

    public ITSCertificateBuilder(ITSCertificate iTSCertificate, ToBeSignedCertificate.Builder builder) {
        this.issuer = iTSCertificate;
        this.tbsCertificateBuilder = builder;
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
    }

    public ITSCertificate getIssuer() {
        return this.issuer;
    }

    public ITSCertificateBuilder setVersion(int n) {
        this.version = new UINT8(n);
        return this;
    }

    public ITSCertificateBuilder setCracaId(byte[] byArray) {
        this.cracaId = new HashedId3(byArray);
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        return this;
    }

    public ITSCertificateBuilder setCrlSeries(int n) {
        this.crlSeries = new CrlSeries(n);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
        return this;
    }

    public ITSCertificateBuilder setValidityPeriod(ITSValidityPeriod iTSValidityPeriod) {
        this.tbsCertificateBuilder.setValidityPeriod(iTSValidityPeriod.toASN1Structure());
        return this;
    }

    public ITSCertificateBuilder setCertIssuePermissions(PsidGroupPermissions ... psidGroupPermissionsArray) {
        this.tbsCertificateBuilder.setCertIssuePermissions(SequenceOfPsidGroupPermissions.builder().addGroupPermission(psidGroupPermissionsArray).createSequenceOfPsidGroupPermissions());
        return this;
    }

    public ITSCertificateBuilder setAppPermissions(PsidSsp ... psidSspArray) {
        SequenceOfPsidSsp.Builder builder = SequenceOfPsidSsp.builder();
        for (int i = 0; i != psidSspArray.length; ++i) {
            builder.setItem(psidSspArray[i]);
        }
        this.tbsCertificateBuilder.setAppPermissions(builder.createSequenceOfPsidSsp());
        return this;
    }
}

