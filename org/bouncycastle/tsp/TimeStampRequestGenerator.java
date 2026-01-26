/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.tsp.TSPIOException;
import org.bouncycastle.tsp.TSPUtil;
import org.bouncycastle.tsp.TimeStampRequest;

public class TimeStampRequestGenerator {
    private static final DefaultDigestAlgorithmIdentifierFinder DEFAULT_DIGEST_ALG_FINDER = new DefaultDigestAlgorithmIdentifierFinder();
    private final ExtensionsGenerator extGenerator = new ExtensionsGenerator();
    private final DigestAlgorithmIdentifierFinder digestAlgFinder;
    private ASN1ObjectIdentifier reqPolicy;
    private ASN1Boolean certReq;

    public TimeStampRequestGenerator() {
        this(DEFAULT_DIGEST_ALG_FINDER);
    }

    public TimeStampRequestGenerator(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        if (digestAlgorithmIdentifierFinder == null) {
            throw new NullPointerException("'digestAlgFinder' cannot be null");
        }
        this.digestAlgFinder = digestAlgorithmIdentifierFinder;
    }

    public void setReqPolicy(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.reqPolicy = aSN1ObjectIdentifier;
    }

    public void setReqPolicy(String string) {
        this.setReqPolicy(new ASN1ObjectIdentifier(string));
    }

    public void setCertReq(ASN1Boolean aSN1Boolean) {
        this.certReq = aSN1Boolean;
    }

    public void setCertReq(boolean bl) {
        this.setCertReq(ASN1Boolean.getInstance(bl));
    }

    public void addExtension(String string, boolean bl, ASN1Encodable aSN1Encodable) throws IOException {
        this.addExtension(new ASN1ObjectIdentifier(string), bl, aSN1Encodable);
    }

    public void addExtension(String string, boolean bl, byte[] byArray) {
        this.addExtension(new ASN1ObjectIdentifier(string), bl, byArray);
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws TSPIOException {
        TSPUtil.addExtension(this.extGenerator, aSN1ObjectIdentifier, bl, aSN1Encodable);
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, byArray);
    }

    public TimeStampRequest generate(String string, byte[] byArray) {
        return this.generate(string, byArray, null);
    }

    public TimeStampRequest generate(String string, byte[] byArray, BigInteger bigInteger) {
        if (string == null) {
            throw new NullPointerException("'digestAlgorithmOID' cannot be null");
        }
        return this.generate(new ASN1ObjectIdentifier(string), byArray, bigInteger);
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] byArray) {
        return this.generate(aSN1ObjectIdentifier, byArray, null);
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] byArray, BigInteger bigInteger) {
        return this.generate(this.digestAlgFinder.find(aSN1ObjectIdentifier), byArray, bigInteger);
    }

    public TimeStampRequest generate(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        return this.generate(algorithmIdentifier, byArray, null);
    }

    public TimeStampRequest generate(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, BigInteger bigInteger) {
        if (algorithmIdentifier == null) {
            throw new NullPointerException("'digestAlgorithmID' cannot be null");
        }
        MessageImprint messageImprint = new MessageImprint(algorithmIdentifier, byArray);
        ASN1Integer aSN1Integer = bigInteger == null ? null : new ASN1Integer(bigInteger);
        Extensions extensions = this.extGenerator.isEmpty() ? null : this.extGenerator.generate();
        return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, aSN1Integer, this.certReq, extensions));
    }
}

