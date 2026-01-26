/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthenticatedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.DefaultAuthenticatedAttributeTableGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataStreamGenerator
extends CMSAuthenticatedGenerator {
    private int bufferSize;
    private boolean berEncodeRecipientSet;
    private MacCalculator macCalculator;

    public void setBufferSize(int n) {
        this.bufferSize = n;
    }

    public void setBEREncodeRecipients(boolean bl) {
        this.berEncodeRecipientSet = bl;
    }

    public OutputStream open(OutputStream outputStream, MacCalculator macCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, outputStream, macCalculator);
    }

    public OutputStream open(OutputStream outputStream, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, outputStream, macCalculator, digestCalculator);
    }

    public OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, MacCalculator macCalculator) throws CMSException {
        return this.open(aSN1ObjectIdentifier, outputStream, macCalculator, null);
    }

    public OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        this.macCalculator = macCalculator;
        try {
            ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(macCalculator.getKey(), this.recipientInfoGenerators);
            BERSequenceGenerator bERSequenceGenerator = new BERSequenceGenerator(outputStream);
            bERSequenceGenerator.addObject(CMSObjectIdentifiers.authenticatedData);
            BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator.getRawOutputStream(), 0, true);
            bERSequenceGenerator2.addObject(new ASN1Integer(AuthenticatedData.calculateVersion(this.originatorInfo)));
            CMSUtils.addOriginatorInfoToGenerator(bERSequenceGenerator2, this.originatorInfo);
            CMSUtils.addRecipientInfosToGenerator(aSN1EncodableVector, bERSequenceGenerator2, this.berEncodeRecipientSet);
            AlgorithmIdentifier algorithmIdentifier = macCalculator.getAlgorithmIdentifier();
            bERSequenceGenerator2.getRawOutputStream().write(algorithmIdentifier.getEncoded());
            if (digestCalculator != null) {
                bERSequenceGenerator2.addObject(new DERTaggedObject(false, 1, (ASN1Encodable)digestCalculator.getAlgorithmIdentifier()));
            }
            BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
            bERSequenceGenerator3.addObject(aSN1ObjectIdentifier);
            OutputStream outputStream2 = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this.bufferSize);
            TeeOutputStream teeOutputStream = digestCalculator != null ? new TeeOutputStream(outputStream2, digestCalculator.getOutputStream()) : new TeeOutputStream(outputStream2, macCalculator.getOutputStream());
            return new CmsAuthenticatedDataOutputStream(macCalculator, digestCalculator, aSN1ObjectIdentifier, teeOutputStream, bERSequenceGenerator, bERSequenceGenerator2, bERSequenceGenerator3);
        }
        catch (IOException iOException) {
            throw new CMSException("exception decoding algorithm parameters.", iOException);
        }
    }

    private class CmsAuthenticatedDataOutputStream
    extends OutputStream {
        private OutputStream dataStream;
        private BERSequenceGenerator cGen;
        private BERSequenceGenerator envGen;
        private BERSequenceGenerator eiGen;
        private MacCalculator macCalculator;
        private DigestCalculator digestCalculator;
        private ASN1ObjectIdentifier contentType;

        public CmsAuthenticatedDataOutputStream(MacCalculator macCalculator, DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, BERSequenceGenerator bERSequenceGenerator, BERSequenceGenerator bERSequenceGenerator2, BERSequenceGenerator bERSequenceGenerator3) {
            this.macCalculator = macCalculator;
            this.digestCalculator = digestCalculator;
            this.contentType = aSN1ObjectIdentifier;
            this.dataStream = outputStream;
            this.cGen = bERSequenceGenerator;
            this.envGen = bERSequenceGenerator2;
            this.eiGen = bERSequenceGenerator3;
        }

        @Override
        public void write(int n) throws IOException {
            this.dataStream.write(n);
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this.dataStream.write(byArray, n, n2);
        }

        @Override
        public void write(byte[] byArray) throws IOException {
            this.dataStream.write(byArray);
        }

        @Override
        public void close() throws IOException {
            Map map;
            this.dataStream.close();
            this.eiGen.close();
            if (this.digestCalculator != null) {
                map = Collections.unmodifiableMap(CMSAuthenticatedDataStreamGenerator.this.getBaseParameters(this.contentType, this.digestCalculator.getAlgorithmIdentifier(), this.macCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
                if (CMSAuthenticatedDataStreamGenerator.this.authGen == null) {
                    CMSAuthenticatedDataStreamGenerator.this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                DERSet dERSet = new DERSet(CMSAuthenticatedDataStreamGenerator.this.authGen.getAttributes(map).toASN1EncodableVector());
                OutputStream outputStream = this.macCalculator.getOutputStream();
                outputStream.write(dERSet.getEncoded("DER"));
                outputStream.close();
                this.envGen.addObject(new DERTaggedObject(false, 2, (ASN1Encodable)dERSet));
            } else {
                map = Collections.EMPTY_MAP;
            }
            this.envGen.addObject(new DEROctetString(this.macCalculator.getMac()));
            CMSUtils.addAttriSetToGenerator(this.envGen, CMSAuthenticatedDataStreamGenerator.this.unauthGen, 3, map);
            this.envGen.close();
            this.cGen.close();
        }
    }
}

