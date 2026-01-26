/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.Provider;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSImplicitCertificateBuilder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaITSImplicitCertificateBuilderBuilder {
    private final JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();

    public JcaITSImplicitCertificateBuilderBuilder setProvider(Provider provider) {
        this.digestCalculatorProviderBuilder.setProvider(provider);
        return this;
    }

    public JcaITSImplicitCertificateBuilderBuilder setProvider(String string) {
        this.digestCalculatorProviderBuilder.setProvider(string);
        return this;
    }

    public ITSImplicitCertificateBuilder build(ITSCertificate iTSCertificate, ToBeSignedCertificate.Builder builder) throws OperatorCreationException {
        return new ITSImplicitCertificateBuilder(iTSCertificate, this.digestCalculatorProviderBuilder.build(), builder);
    }
}

