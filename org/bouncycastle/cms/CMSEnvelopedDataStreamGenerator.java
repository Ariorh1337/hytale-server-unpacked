/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataStreamGenerator
extends CMSEnvelopedGenerator {
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;

    public void setBufferSize(int n) {
        this._bufferSize = n;
    }

    public void setBEREncodeRecipients(boolean bl) {
        this._berEncodeRecipientSet = bl;
    }

    private ASN1Integer getVersion(ASN1EncodableVector aSN1EncodableVector) {
        if (this.unprotectedAttributeGenerator != null) {
            return new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, new DLSet(aSN1EncodableVector), new DLSet()));
        }
        return new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, new DLSet(aSN1EncodableVector), null));
    }

    private OutputStream doOpen(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, OutputEncryptor outputEncryptor) throws IOException, CMSException {
        ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(outputEncryptor.getKey(), this.recipientInfoGenerators);
        return this.open(aSN1ObjectIdentifier, outputStream, aSN1EncodableVector, outputEncryptor);
    }

    protected OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputEncryptor outputEncryptor) throws IOException {
        BERSequenceGenerator bERSequenceGenerator = new BERSequenceGenerator(outputStream);
        bERSequenceGenerator.addObject(CMSObjectIdentifiers.envelopedData);
        BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator.getRawOutputStream(), 0, true);
        bERSequenceGenerator2.addObject(this.getVersion(aSN1EncodableVector));
        CMSUtils.addOriginatorInfoToGenerator(bERSequenceGenerator2, this.originatorInfo);
        CMSUtils.addRecipientInfosToGenerator(aSN1EncodableVector, bERSequenceGenerator2, this._berEncodeRecipientSet);
        BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
        bERSequenceGenerator3.addObject(aSN1ObjectIdentifier);
        AlgorithmIdentifier algorithmIdentifier = outputEncryptor.getAlgorithmIdentifier();
        bERSequenceGenerator3.getRawOutputStream().write(algorithmIdentifier.getEncoded());
        OutputStream outputStream2 = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, false, this._bufferSize);
        return new CmsEnvelopedDataOutputStream(outputEncryptor, outputStream2, bERSequenceGenerator, bERSequenceGenerator2, bERSequenceGenerator3);
    }

    protected OutputStream open(OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputEncryptor outputEncryptor) throws CMSException {
        try {
            return this.open(CMSObjectIdentifiers.data, outputStream, aSN1EncodableVector, outputEncryptor);
        }
        catch (IOException iOException) {
            throw new CMSException("exception decoding algorithm parameters.", iOException);
        }
    }

    public OutputStream open(OutputStream outputStream, OutputEncryptor outputEncryptor) throws CMSException, IOException {
        return this.doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), outputStream, outputEncryptor);
    }

    public OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, OutputEncryptor outputEncryptor) throws CMSException, IOException {
        return this.doOpen(aSN1ObjectIdentifier, outputStream, outputEncryptor);
    }

    private class CmsEnvelopedDataOutputStream
    extends OutputStream {
        private final OutputEncryptor _encryptor;
        private final OutputStream _cOut;
        private OutputStream _octetStream;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _envGen;
        private BERSequenceGenerator _eiGen;

        public CmsEnvelopedDataOutputStream(OutputEncryptor outputEncryptor, OutputStream outputStream, BERSequenceGenerator bERSequenceGenerator, BERSequenceGenerator bERSequenceGenerator2, BERSequenceGenerator bERSequenceGenerator3) {
            this._encryptor = outputEncryptor;
            this._octetStream = outputStream;
            this._cOut = outputEncryptor.getOutputStream(outputStream);
            this._cGen = bERSequenceGenerator;
            this._envGen = bERSequenceGenerator2;
            this._eiGen = bERSequenceGenerator3;
        }

        @Override
        public void write(int n) throws IOException {
            this._cOut.write(n);
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this._cOut.write(byArray, n, n2);
        }

        @Override
        public void write(byte[] byArray) throws IOException {
            this._cOut.write(byArray);
        }

        @Override
        public void close() throws IOException {
            this._cOut.close();
            if (this._encryptor instanceof OutputAEADEncryptor) {
                this._octetStream.write(((OutputAEADEncryptor)this._encryptor).getMAC());
                this._octetStream.close();
            }
            this._eiGen.close();
            CMSUtils.addAttriSetToGenerator(this._envGen, CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator, 1, Collections.EMPTY_MAP);
            this._envGen.close();
            this._cGen.close();
        }
    }
}

