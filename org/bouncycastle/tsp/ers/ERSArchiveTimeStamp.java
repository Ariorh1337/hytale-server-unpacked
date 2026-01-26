/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.ers.ArchiveTimeStampValidationException;
import org.bouncycastle.tsp.ers.BinaryTreeRootCalculator;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Store;

public class ERSArchiveTimeStamp {
    private final ArchiveTimeStamp archiveTimeStamp;
    private final DigestCalculator digCalc;
    private final TimeStampToken timeStampToken;
    private final byte[] previousChainsDigest;
    private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();

    public ERSArchiveTimeStamp(byte[] byArray, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this(ArchiveTimeStamp.getInstance(byArray), digestCalculatorProvider);
    }

    public ERSArchiveTimeStamp(ArchiveTimeStamp archiveTimeStamp, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this.previousChainsDigest = null;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digestCalculatorProvider.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (IOException iOException) {
            throw new ERSException(iOException.getMessage(), iOException);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new ERSException(operatorCreationException.getMessage(), operatorCreationException);
        }
    }

    ERSArchiveTimeStamp(ArchiveTimeStamp archiveTimeStamp, DigestCalculator digestCalculator) throws TSPException, ERSException {
        this.previousChainsDigest = null;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digestCalculator;
        }
        catch (IOException iOException) {
            throw new ERSException(iOException.getMessage(), iOException);
        }
    }

    ERSArchiveTimeStamp(byte[] byArray, ArchiveTimeStamp archiveTimeStamp, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this.previousChainsDigest = byArray;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digestCalculatorProvider.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (IOException iOException) {
            throw new ERSException(iOException.getMessage(), iOException);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new ERSException(operatorCreationException.getMessage(), operatorCreationException);
        }
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.archiveTimeStamp.getDigestAlgorithmIdentifier();
    }

    public void validatePresent(ERSData eRSData, Date date) throws ERSException {
        this.validatePresent(eRSData instanceof ERSDataGroup, eRSData.getHash(this.digCalc, this.previousChainsDigest), date);
    }

    public boolean isContaining(ERSData eRSData, Date date) throws ERSException {
        if (this.timeStampToken.getTimeStampInfo().getGenTime().after(date)) {
            throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
        }
        try {
            this.validatePresent(eRSData, date);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public void validatePresent(boolean bl, byte[] byArray, Date date) throws ERSException {
        if (this.timeStampToken.getTimeStampInfo().getGenTime().after(date)) {
            throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
        }
        this.checkContainsHashValue(bl, byArray, this.digCalc);
        PartialHashtree[] partialHashtreeArray = this.archiveTimeStamp.getReducedHashTree();
        byte[] byArray2 = partialHashtreeArray != null ? this.rootNodeCalculator.recoverRootHash(this.digCalc, this.archiveTimeStamp.getReducedHashTree()) : byArray;
        this.checkTimeStampValid(this.timeStampToken, byArray2);
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public X509CertificateHolder getSigningCertificate() {
        Collection<X509CertificateHolder> collection;
        Store<X509CertificateHolder> store = this.timeStampToken.getCertificates();
        if (store != null && !(collection = store.getMatches(this.timeStampToken.getSID())).isEmpty()) {
            return collection.iterator().next();
        }
        return null;
    }

    public void validate(SignerInformationVerifier signerInformationVerifier) throws TSPException {
        this.timeStampToken.validate(signerInformationVerifier);
    }

    void checkContainsHashValue(boolean bl, byte[] byArray, DigestCalculator digestCalculator) throws ArchiveTimeStampValidationException {
        PartialHashtree[] partialHashtreeArray = this.archiveTimeStamp.getReducedHashTree();
        if (partialHashtreeArray != null) {
            PartialHashtree partialHashtree = partialHashtreeArray[0];
            if (!bl && partialHashtree.containsHash(byArray)) {
                return;
            }
            if (partialHashtree.getValueCount() > 1 && Arrays.areEqual(byArray, ERSUtil.calculateBranchHash(digestCalculator, partialHashtree.getValues()))) {
                return;
            }
            throw new ArchiveTimeStampValidationException("object hash not found");
        }
        if (!Arrays.areEqual(byArray, this.timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ArchiveTimeStampValidationException("object hash not found in wrapped timestamp");
        }
    }

    void checkTimeStampValid(TimeStampToken timeStampToken, byte[] byArray) throws ArchiveTimeStampValidationException {
        if (byArray != null && !Arrays.areEqual(byArray, timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ArchiveTimeStampValidationException("timestamp hash does not match root");
        }
    }

    public Date getGenTime() {
        return this.timeStampToken.getTimeStampInfo().getGenTime();
    }

    public Date getExpiryTime() {
        X509CertificateHolder x509CertificateHolder = this.getSigningCertificate();
        if (x509CertificateHolder != null) {
            return x509CertificateHolder.getNotAfter();
        }
        return null;
    }

    public ArchiveTimeStamp toASN1Structure() {
        return this.archiveTimeStamp;
    }

    public byte[] getEncoded() throws IOException {
        return this.archiveTimeStamp.getEncoded();
    }

    public static ERSArchiveTimeStamp fromTimeStampToken(TimeStampToken timeStampToken, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        return new ERSArchiveTimeStamp(new ArchiveTimeStamp(timeStampToken.toCMSSignedData().toASN1Structure()), digestCalculatorProvider);
    }
}

