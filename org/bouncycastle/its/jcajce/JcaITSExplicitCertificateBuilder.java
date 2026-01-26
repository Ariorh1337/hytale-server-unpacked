/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.jcajce.JcaITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.JceITSPublicEncryptionKey;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class JcaITSExplicitCertificateBuilder
extends ITSExplicitCertificateBuilder {
    private JcaJceHelper helper;

    public JcaITSExplicitCertificateBuilder(ITSContentSigner iTSContentSigner, ToBeSignedCertificate.Builder builder) {
        this(iTSContentSigner, builder, new DefaultJcaJceHelper());
    }

    private JcaITSExplicitCertificateBuilder(ITSContentSigner iTSContentSigner, ToBeSignedCertificate.Builder builder, JcaJceHelper jcaJceHelper) {
        super(iTSContentSigner, builder);
        this.helper = jcaJceHelper;
    }

    public JcaITSExplicitCertificateBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcaITSExplicitCertificateBuilder setProvider(String string) {
        this.helper = new NamedJcaJceHelper(string);
        return this;
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKey eCPublicKey) {
        return this.build(certificateId, eCPublicKey, null);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKey eCPublicKey, ECPublicKey eCPublicKey2) {
        JceITSPublicEncryptionKey jceITSPublicEncryptionKey = null;
        if (eCPublicKey2 != null) {
            jceITSPublicEncryptionKey = new JceITSPublicEncryptionKey(eCPublicKey2, this.helper);
        }
        return super.build(certificateId, new JcaITSPublicVerificationKey(eCPublicKey, this.helper), jceITSPublicEncryptionKey);
    }
}

