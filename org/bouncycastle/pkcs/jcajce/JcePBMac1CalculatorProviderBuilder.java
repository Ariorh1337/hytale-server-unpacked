/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs.jcajce;

import java.security.Provider;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.pkcs.jcajce.JcePBMac1CalculatorBuilder;

public class JcePBMac1CalculatorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JcePBMac1CalculatorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePBMac1CalculatorProviderBuilder setProvider(String string) {
        this.helper = new NamedJcaJceHelper(string);
        return this;
    }

    public PBEMacCalculatorProvider build() {
        return new PBEMacCalculatorProvider(){

            @Override
            public MacCalculator get(AlgorithmIdentifier algorithmIdentifier, char[] cArray) throws OperatorCreationException {
                if (!PKCSObjectIdentifiers.id_PBMAC1.equals(algorithmIdentifier.getAlgorithm())) {
                    throw new OperatorCreationException("protection algorithm not PB mac based");
                }
                JcePBMac1CalculatorBuilder jcePBMac1CalculatorBuilder = new JcePBMac1CalculatorBuilder(PBMAC1Params.getInstance(algorithmIdentifier.getParameters())).setHelper(JcePBMac1CalculatorProviderBuilder.this.helper);
                return jcePBMac1CalculatorBuilder.build(cArray);
            }
        };
    }
}

