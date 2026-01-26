/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.bc;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.bc.BcITSPublicEncryptionKey;
import org.bouncycastle.its.bc.BcITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class BcITSExplicitCertificateBuilder
extends ITSExplicitCertificateBuilder {
    public BcITSExplicitCertificateBuilder(ITSContentSigner iTSContentSigner, ToBeSignedCertificate.Builder builder) {
        super(iTSContentSigner, builder);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters eCPublicKeyParameters) {
        return this.build(certificateId, eCPublicKeyParameters, null);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters eCPublicKeyParameters, ECPublicKeyParameters eCPublicKeyParameters2) {
        BcITSPublicEncryptionKey bcITSPublicEncryptionKey = null;
        if (eCPublicKeyParameters2 != null) {
            bcITSPublicEncryptionKey = new BcITSPublicEncryptionKey(eCPublicKeyParameters2);
        }
        return super.build(certificateId, new BcITSPublicVerificationKey(eCPublicKeyParameters), bcITSPublicEncryptionKey);
    }
}

