/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.bc.BcPKCS12PBMac1CalculatorBuilder;

public class BcPKCS12PBMac1CalculatorBuilderProvider
implements PKCS12MacCalculatorBuilderProvider {
    @Override
    public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
        return new PKCS12MacCalculatorBuilder(){
            final /* synthetic */ BcPKCS12PBMac1CalculatorBuilderProvider this$0;
            {
                this.this$0 = bcPKCS12PBMac1CalculatorBuilderProvider;
            }

            @Override
            public MacCalculator build(char[] cArray) throws OperatorCreationException {
                BcPKCS12PBMac1CalculatorBuilder bcPKCS12PBMac1CalculatorBuilder;
                if (!PKCSObjectIdentifiers.id_PBMAC1.equals(algorithmIdentifier.getAlgorithm())) {
                    throw new OperatorCreationException("protection algorithm not PB mac based");
                }
                try {
                    bcPKCS12PBMac1CalculatorBuilder = new BcPKCS12PBMac1CalculatorBuilder(PBMAC1Params.getInstance(algorithmIdentifier.getParameters()));
                }
                catch (IOException iOException) {
                    throw new OperatorCreationException("invalid parameters in protection algorithm: " + iOException.getMessage());
                }
                return bcPKCS12PBMac1CalculatorBuilder.build(cArray);
            }

            @Override
            public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), algorithmIdentifier.getParameters());
            }
        };
    }
}

