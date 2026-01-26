/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputAEADEncryptor;

public class CMSAuthEnvelopedDataStreamGenerator
extends CMSAuthEnvelopedGenerator {
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;

    public void setBufferSize(int n) {
        this._bufferSize = n;
    }

    public void setBEREncodeRecipients(boolean bl) {
        this._berEncodeRecipientSet = bl;
    }

    private OutputStream doOpen(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, OutputAEADEncryptor outputAEADEncryptor) throws IOException, CMSException {
        ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(outputAEADEncryptor.getKey(), this.recipientInfoGenerators);
        return this.open(aSN1ObjectIdentifier, outputStream, aSN1EncodableVector, outputAEADEncryptor);
    }

    protected OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputAEADEncryptor outputAEADEncryptor) throws IOException {
        BERSequenceGenerator bERSequenceGenerator = new BERSequenceGenerator(outputStream);
        bERSequenceGenerator.addObject(CMSObjectIdentifiers.authEnvelopedData);
        BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator.getRawOutputStream(), 0, true);
        bERSequenceGenerator2.addObject(new ASN1Integer(0L));
        CMSUtils.addOriginatorInfoToGenerator(bERSequenceGenerator2, this.originatorInfo);
        CMSUtils.addRecipientInfosToGenerator(aSN1EncodableVector, bERSequenceGenerator2, this._berEncodeRecipientSet);
        BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
        bERSequenceGenerator3.addObject(aSN1ObjectIdentifier);
        AlgorithmIdentifier algorithmIdentifier = outputAEADEncryptor.getAlgorithmIdentifier();
        bERSequenceGenerator3.getRawOutputStream().write(algorithmIdentifier.getEncoded());
        OutputStream outputStream2 = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize);
        return new CMSAuthEnvelopedDataOutputStream(outputAEADEncryptor, outputStream2, bERSequenceGenerator, bERSequenceGenerator2, bERSequenceGenerator3);
    }

    protected OutputStream open(OutputStream outputStream, ASN1EncodableVector aSN1EncodableVector, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        try {
            return this.open(CMSObjectIdentifiers.data, outputStream, aSN1EncodableVector, outputAEADEncryptor);
        }
        catch (IOException iOException) {
            throw new CMSException("exception decoding algorithm parameters.", iOException);
        }
    }

    public OutputStream open(OutputStream outputStream, OutputAEADEncryptor outputAEADEncryptor) throws CMSException, IOException {
        return this.doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), outputStream, outputAEADEncryptor);
    }

    private class CMSAuthEnvelopedDataOutputStream
    extends OutputStream {
        private final OutputAEADEncryptor _encryptor;
        private final OutputStream _cOut;
        private final OutputStream _octetStream;
        private final BERSequenceGenerator _cGen;
        private final BERSequenceGenerator _envGen;
        private final BERSequenceGenerator _eiGen;

        public CMSAuthEnvelopedDataOutputStream(OutputAEADEncryptor outputAEADEncryptor, OutputStream outputStream, BERSequenceGenerator bERSequenceGenerator, BERSequenceGenerator bERSequenceGenerator2, BERSequenceGenerator bERSequenceGenerator3) {
            this._encryptor = outputAEADEncryptor;
            this._octetStream = outputStream;
            this._cOut = outputAEADEncryptor.getOutputStream(outputStream);
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
            ASN1Set aSN1Set = CMSUtils.processAuthAttrSet(CMSAuthEnvelopedDataStreamGenerator.this.authAttrsGenerator, this._encryptor);
            this._cOut.close();
            this._octetStream.close();
            this._eiGen.close();
            if (aSN1Set != null) {
                this._envGen.addObject(new DERTaggedObject(false, 1, (ASN1Encodable)aSN1Set));
            }
            this._envGen.addObject(new DEROctetString(this._encryptor.getMAC()));
            CMSUtils.addAttriSetToGenerator(this._envGen, CMSAuthEnvelopedDataStreamGenerator.this.unauthAttrsGenerator, 2, Collections.EMPTY_MAP);
            this._envGen.close();
            this._cGen.close();
        }
    }
}

