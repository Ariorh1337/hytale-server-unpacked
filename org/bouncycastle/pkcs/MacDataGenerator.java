/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.Strings;

class MacDataGenerator {
    private PKCS12MacCalculatorBuilder builder;

    MacDataGenerator(PKCS12MacCalculatorBuilder pKCS12MacCalculatorBuilder) {
        this.builder = pKCS12MacCalculatorBuilder;
    }

    public MacData build(char[] cArray, byte[] byArray) throws PKCSException {
        int n;
        byte[] byArray2;
        Object object;
        MacCalculator macCalculator;
        try {
            macCalculator = this.builder.build(cArray);
            object = macCalculator.getOutputStream();
            ((OutputStream)object).write(byArray);
            ((OutputStream)object).close();
        }
        catch (Exception exception) {
            throw new PKCSException("unable to process data: " + exception.getMessage(), exception);
        }
        object = macCalculator.getAlgorithmIdentifier();
        DigestInfo digestInfo = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), macCalculator.getMac());
        if (PKCSObjectIdentifiers.id_PBMAC1.equals(digestInfo.getAlgorithmId().getAlgorithm())) {
            byArray2 = Strings.toUTF8ByteArray("NOT USED".toCharArray());
            n = 1;
        } else {
            PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(((AlgorithmIdentifier)object).getParameters());
            byArray2 = pKCS12PBEParams.getIV();
            n = pKCS12PBEParams.getIterations().intValue();
        }
        return new MacData(digestInfo, byArray2, n);
    }
}

