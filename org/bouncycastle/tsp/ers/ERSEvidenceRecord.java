/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStampGenerator;
import org.bouncycastle.tsp.ers.ERSByteData;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.util.io.Streams;

public class ERSEvidenceRecord {
    private final EvidenceRecord evidenceRecord;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private final ERSArchiveTimeStamp firstArchiveTimeStamp;
    private final ERSArchiveTimeStamp lastArchiveTimeStamp;
    private final byte[] previousChainsDigest;
    private final DigestCalculator digCalc;
    private final ArchiveTimeStamp primaryArchiveTimeStamp;

    public ERSEvidenceRecord(InputStream inputStream, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException, IOException {
        this(EvidenceRecord.getInstance(Streams.readAll(inputStream)), digestCalculatorProvider);
    }

    public ERSEvidenceRecord(byte[] byArray, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this(EvidenceRecord.getInstance(byArray), digestCalculatorProvider);
    }

    public ERSEvidenceRecord(EvidenceRecord evidenceRecord, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this.evidenceRecord = evidenceRecord;
        this.digestCalculatorProvider = digestCalculatorProvider;
        ArchiveTimeStampSequence archiveTimeStampSequence = evidenceRecord.getArchiveTimeStampSequence();
        ArchiveTimeStampChain[] archiveTimeStampChainArray = archiveTimeStampSequence.getArchiveTimeStampChains();
        this.primaryArchiveTimeStamp = archiveTimeStampChainArray[0].getArchiveTimestamps()[0];
        this.validateChains(archiveTimeStampChainArray);
        ArchiveTimeStampChain archiveTimeStampChain = archiveTimeStampChainArray[archiveTimeStampChainArray.length - 1];
        ArchiveTimeStamp[] archiveTimeStampArray = archiveTimeStampChain.getArchiveTimestamps();
        this.lastArchiveTimeStamp = new ERSArchiveTimeStamp(archiveTimeStampArray[archiveTimeStampArray.length - 1], digestCalculatorProvider);
        if (archiveTimeStampChainArray.length > 1) {
            try {
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                for (int i = 0; i != archiveTimeStampChainArray.length - 1; ++i) {
                    aSN1EncodableVector.add(archiveTimeStampChainArray[i]);
                }
                this.digCalc = digestCalculatorProvider.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
                OutputStream outputStream = this.digCalc.getOutputStream();
                outputStream.write(new DERSequence(aSN1EncodableVector).getEncoded("DER"));
                outputStream.close();
                this.previousChainsDigest = this.digCalc.getDigest();
            }
            catch (Exception exception) {
                throw new ERSException(exception.getMessage(), exception);
            }
        } else {
            this.digCalc = null;
            this.previousChainsDigest = null;
        }
        this.firstArchiveTimeStamp = new ERSArchiveTimeStamp(this.previousChainsDigest, archiveTimeStampArray[0], digestCalculatorProvider);
    }

    private void validateChains(ArchiveTimeStampChain[] archiveTimeStampChainArray) throws ERSException, TSPException {
        for (int i = 0; i != archiveTimeStampChainArray.length; ++i) {
            ArchiveTimeStamp[] archiveTimeStampArray = archiveTimeStampChainArray[i].getArchiveTimestamps();
            ArchiveTimeStamp archiveTimeStamp = archiveTimeStampArray[0];
            AlgorithmIdentifier algorithmIdentifier = archiveTimeStampArray[0].getDigestAlgorithmIdentifier();
            for (int j = 1; j != archiveTimeStampArray.length; ++j) {
                ArchiveTimeStamp archiveTimeStamp2 = archiveTimeStampArray[j];
                if (!algorithmIdentifier.equals(archiveTimeStamp2.getDigestAlgorithmIdentifier())) {
                    throw new ERSException("invalid digest algorithm in chain");
                }
                ContentInfo contentInfo = archiveTimeStamp2.getTimeStamp();
                if (!contentInfo.getContentType().equals(CMSObjectIdentifiers.signedData)) {
                    throw new TSPException("cannot identify TSTInfo");
                }
                TSTInfo tSTInfo = this.extractTimeStamp(contentInfo);
                try {
                    DigestCalculator digestCalculator = this.digestCalculatorProvider.get(algorithmIdentifier);
                    ERSArchiveTimeStamp eRSArchiveTimeStamp = new ERSArchiveTimeStamp(archiveTimeStamp2, digestCalculator);
                    eRSArchiveTimeStamp.validatePresent(new ERSByteData(archiveTimeStamp.getTimeStamp().getEncoded("DER")), tSTInfo.getGenTime().getDate());
                }
                catch (Exception exception) {
                    throw new ERSException("invalid timestamp renewal found: " + exception.getMessage(), exception);
                }
                archiveTimeStamp = archiveTimeStamp2;
            }
        }
    }

    ArchiveTimeStamp[] getArchiveTimeStamps() {
        ArchiveTimeStampSequence archiveTimeStampSequence = this.evidenceRecord.getArchiveTimeStampSequence();
        ArchiveTimeStampChain[] archiveTimeStampChainArray = archiveTimeStampSequence.getArchiveTimeStampChains();
        ArchiveTimeStampChain archiveTimeStampChain = archiveTimeStampChainArray[archiveTimeStampChainArray.length - 1];
        return archiveTimeStampChain.getArchiveTimestamps();
    }

    public byte[] getPrimaryRootHash() throws TSPException, ERSException {
        ContentInfo contentInfo = this.primaryArchiveTimeStamp.getTimeStamp();
        if (contentInfo.getContentType().equals(CMSObjectIdentifiers.signedData)) {
            TSTInfo tSTInfo = this.extractTimeStamp(contentInfo);
            return tSTInfo.getMessageImprint().getHashedMessage();
        }
        throw new ERSException("cannot identify TSTInfo for digest");
    }

    private TSTInfo extractTimeStamp(ContentInfo contentInfo) throws TSPException {
        SignedData signedData = SignedData.getInstance(contentInfo.getContent());
        if (signedData.getEncapContentInfo().getContentType().equals(PKCSObjectIdentifiers.id_ct_TSTInfo)) {
            TSTInfo tSTInfo = TSTInfo.getInstance(ASN1OctetString.getInstance(signedData.getEncapContentInfo().getContent()).getOctets());
            return tSTInfo;
        }
        throw new TSPException("cannot parse time stamp");
    }

    public boolean isRelatedTo(ERSEvidenceRecord eRSEvidenceRecord) {
        return this.primaryArchiveTimeStamp.getTimeStamp().equals(eRSEvidenceRecord.primaryArchiveTimeStamp.getTimeStamp());
    }

    public boolean isContaining(ERSData eRSData, Date date) throws ERSException {
        return this.firstArchiveTimeStamp.isContaining(eRSData, date);
    }

    public void validatePresent(ERSData eRSData, Date date) throws ERSException {
        this.firstArchiveTimeStamp.validatePresent(eRSData, date);
    }

    public void validatePresent(boolean bl, byte[] byArray, Date date) throws ERSException {
        this.firstArchiveTimeStamp.validatePresent(bl, byArray, date);
    }

    public X509CertificateHolder getSigningCertificate() {
        return this.lastArchiveTimeStamp.getSigningCertificate();
    }

    public void validate(SignerInformationVerifier signerInformationVerifier) throws TSPException {
        if (this.firstArchiveTimeStamp != this.lastArchiveTimeStamp) {
            ArchiveTimeStamp[] archiveTimeStampArray = this.getArchiveTimeStamps();
            for (int i = 0; i != archiveTimeStampArray.length - 1; ++i) {
                try {
                    this.lastArchiveTimeStamp.validatePresent(new ERSByteData(archiveTimeStampArray[i].getTimeStamp().getEncoded("DER")), this.lastArchiveTimeStamp.getGenTime());
                    continue;
                }
                catch (Exception exception) {
                    throw new TSPException("unable to process previous ArchiveTimeStamps", exception);
                }
            }
        }
        this.lastArchiveTimeStamp.validate(signerInformationVerifier);
    }

    public EvidenceRecord toASN1Structure() {
        return this.evidenceRecord;
    }

    public byte[] getEncoded() throws IOException {
        return this.evidenceRecord.getEncoded();
    }

    public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator timeStampRequestGenerator) throws TSPException, ERSException {
        return this.generateTimeStampRenewalRequest(timeStampRequestGenerator, null);
    }

    public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator timeStampRequestGenerator, BigInteger bigInteger) throws ERSException, TSPException {
        ERSArchiveTimeStampGenerator eRSArchiveTimeStampGenerator = this.buildTspRenewalGenerator();
        try {
            return eRSArchiveTimeStampGenerator.generateTimeStampRequest(timeStampRequestGenerator, bigInteger);
        }
        catch (IOException iOException) {
            throw new ERSException(iOException.getMessage(), iOException);
        }
    }

    public ERSEvidenceRecord renewTimeStamp(TimeStampResponse timeStampResponse) throws ERSException, TSPException {
        ERSArchiveTimeStampGenerator eRSArchiveTimeStampGenerator = this.buildTspRenewalGenerator();
        ArchiveTimeStamp archiveTimeStamp = eRSArchiveTimeStampGenerator.generateArchiveTimeStamp(timeStampResponse).toASN1Structure();
        try {
            return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(archiveTimeStamp, false), this.digestCalculatorProvider);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new ERSException(illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

    private ERSArchiveTimeStampGenerator buildTspRenewalGenerator() throws ERSException {
        DigestCalculator digestCalculator;
        try {
            digestCalculator = this.digestCalculatorProvider.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new ERSException(operatorCreationException.getMessage(), operatorCreationException);
        }
        ArchiveTimeStamp[] archiveTimeStampArray = this.getArchiveTimeStamps();
        if (!digestCalculator.getAlgorithmIdentifier().equals(archiveTimeStampArray[0].getDigestAlgorithmIdentifier())) {
            throw new ERSException("digest mismatch for timestamp renewal");
        }
        ERSArchiveTimeStampGenerator eRSArchiveTimeStampGenerator = new ERSArchiveTimeStampGenerator(digestCalculator);
        ArrayList<ERSData> arrayList = new ArrayList<ERSData>(archiveTimeStampArray.length);
        for (int i = 0; i != archiveTimeStampArray.length; ++i) {
            try {
                arrayList.add(new ERSByteData(archiveTimeStampArray[i].getTimeStamp().getEncoded("DER")));
                continue;
            }
            catch (IOException iOException) {
                throw new ERSException("unable to process previous ArchiveTimeStamps", iOException);
            }
        }
        ERSDataGroup eRSDataGroup = new ERSDataGroup(arrayList);
        eRSArchiveTimeStampGenerator.addData(eRSDataGroup);
        return eRSArchiveTimeStampGenerator;
    }

    public TimeStampRequest generateHashRenewalRequest(DigestCalculator digestCalculator, ERSData eRSData, TimeStampRequestGenerator timeStampRequestGenerator) throws ERSException, TSPException, IOException {
        return this.generateHashRenewalRequest(digestCalculator, eRSData, timeStampRequestGenerator, null);
    }

    public TimeStampRequest generateHashRenewalRequest(DigestCalculator digestCalculator, ERSData eRSData, TimeStampRequestGenerator timeStampRequestGenerator, BigInteger bigInteger) throws ERSException, TSPException, IOException {
        try {
            this.firstArchiveTimeStamp.validatePresent(eRSData, new Date());
        }
        catch (Exception exception) {
            throw new ERSException("attempt to hash renew on invalid data");
        }
        ERSArchiveTimeStampGenerator eRSArchiveTimeStampGenerator = new ERSArchiveTimeStampGenerator(digestCalculator);
        eRSArchiveTimeStampGenerator.addData(eRSData);
        eRSArchiveTimeStampGenerator.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
        return eRSArchiveTimeStampGenerator.generateTimeStampRequest(timeStampRequestGenerator, bigInteger);
    }

    public ERSEvidenceRecord renewHash(DigestCalculator digestCalculator, ERSData eRSData, TimeStampResponse timeStampResponse) throws ERSException, TSPException {
        try {
            this.firstArchiveTimeStamp.validatePresent(eRSData, new Date());
        }
        catch (Exception exception) {
            throw new ERSException("attempt to hash renew on invalid data");
        }
        try {
            ERSArchiveTimeStampGenerator eRSArchiveTimeStampGenerator = new ERSArchiveTimeStampGenerator(digestCalculator);
            eRSArchiveTimeStampGenerator.addData(eRSData);
            eRSArchiveTimeStampGenerator.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
            ArchiveTimeStamp archiveTimeStamp = eRSArchiveTimeStampGenerator.generateArchiveTimeStamp(timeStampResponse).toASN1Structure();
            return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(archiveTimeStamp, true), this.digestCalculatorProvider);
        }
        catch (IOException iOException) {
            throw new ERSException(iOException.getMessage(), iOException);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new ERSException(illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

    DigestCalculatorProvider getDigestAlgorithmProvider() {
        return this.digestCalculatorProvider;
    }
}

