/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPRuntimeException;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

class CMPUtil {
    CMPUtil() {
    }

    static byte[] calculateCertHash(ASN1Object aSN1Object, AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) throws CMPException {
        AlgorithmIdentifier algorithmIdentifier2 = digestAlgorithmIdentifierFinder.find(algorithmIdentifier);
        if (algorithmIdentifier2 == null) {
            throw new CMPException("cannot find digest algorithm from signature algorithm");
        }
        return CMPUtil.calculateDigest(aSN1Object, algorithmIdentifier2, digestCalculatorProvider);
    }

    static byte[] calculateDigest(ASN1Object aSN1Object, AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        DigestCalculator digestCalculator = CMPUtil.getDigestCalculator(algorithmIdentifier, digestCalculatorProvider);
        CMPUtil.derEncodeToStream(aSN1Object, digestCalculator.getOutputStream());
        return digestCalculator.getDigest();
    }

    static void derEncodeToStream(ASN1Object aSN1Object, OutputStream outputStream) {
        try {
            aSN1Object.encodeTo(outputStream, "DER");
            outputStream.close();
        }
        catch (IOException iOException) {
            throw new CMPRuntimeException("unable to DER encode object: " + iOException.getMessage(), iOException);
        }
    }

    static DigestCalculator getDigestCalculator(AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        try {
            return digestCalculatorProvider.get(algorithmIdentifier);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new CMPException("unable to create digester: " + operatorCreationException.getMessage(), operatorCreationException);
        }
    }
}

