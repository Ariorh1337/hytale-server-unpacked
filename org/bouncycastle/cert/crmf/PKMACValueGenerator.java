/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.operator.MacCalculator;

abstract class PKMACValueGenerator {
    private PKMACValueGenerator() {
    }

    public static PKMACValue generate(PKMACBuilder pKMACBuilder, char[] cArray, SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        MacCalculator macCalculator = pKMACBuilder.build(cArray);
        OutputStream outputStream = macCalculator.getOutputStream();
        try {
            outputStream.write(subjectPublicKeyInfo.getEncoded("DER"));
            outputStream.close();
        }
        catch (IOException iOException) {
            throw new CRMFException("exception encoding mac input: " + iOException.getMessage(), iOException);
        }
        return new PKMACValue(macCalculator.getAlgorithmIdentifier(), new DERBitString(macCalculator.getMac()));
    }
}

