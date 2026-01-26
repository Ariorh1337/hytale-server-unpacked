/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

public class BasicConstraintsValidation
implements CertPathValidation {
    private boolean previousCertWasCA = true;
    private Integer maxPathLength = null;
    private boolean isMandatory = true;

    public BasicConstraintsValidation() {
        this(true);
    }

    public BasicConstraintsValidation(boolean bl) {
        this.isMandatory = bl;
    }

    @Override
    public void validate(CertPathValidationContext certPathValidationContext, X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        ASN1Integer aSN1Integer;
        certPathValidationContext.addHandledExtension(Extension.basicConstraints);
        if (!this.previousCertWasCA) {
            throw new CertPathValidationException("Basic constraints violated: issuer is not a CA");
        }
        BasicConstraints basicConstraints = BasicConstraints.fromExtensions(x509CertificateHolder.getExtensions());
        boolean bl = this.previousCertWasCA = basicConstraints != null && basicConstraints.isCA() || basicConstraints == null && !this.isMandatory;
        if (this.maxPathLength != null && !x509CertificateHolder.getSubject().equals(x509CertificateHolder.getIssuer())) {
            if (this.maxPathLength < 0) {
                throw new CertPathValidationException("Basic constraints violated: path length exceeded");
            }
            this.maxPathLength = Integers.valueOf(this.maxPathLength - 1);
        }
        if (basicConstraints != null && basicConstraints.isCA() && (aSN1Integer = basicConstraints.getPathLenConstraintInteger()) != null) {
            int n = aSN1Integer.intPositiveValueExact();
            if (this.maxPathLength == null || n < this.maxPathLength) {
                this.maxPathLength = Integers.valueOf(n);
            }
        }
    }

    @Override
    public Memoable copy() {
        BasicConstraintsValidation basicConstraintsValidation = new BasicConstraintsValidation();
        basicConstraintsValidation.isMandatory = this.isMandatory;
        basicConstraintsValidation.previousCertWasCA = this.previousCertWasCA;
        basicConstraintsValidation.maxPathLength = this.maxPathLength;
        return basicConstraintsValidation;
    }

    @Override
    public void reset(Memoable memoable) {
        BasicConstraintsValidation basicConstraintsValidation = (BasicConstraintsValidation)memoable;
        this.isMandatory = basicConstraintsValidation.isMandatory;
        this.previousCertWasCA = basicConstraintsValidation.previousCertWasCA;
        this.maxPathLength = basicConstraintsValidation.maxPathLength;
    }
}

