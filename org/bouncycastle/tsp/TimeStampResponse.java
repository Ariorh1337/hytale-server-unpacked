/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Arrays;

public class TimeStampResponse {
    private final TimeStampResp resp;
    private final TimeStampToken timeStampToken;

    private static TimeStampResp parseTimeStampResp(byte[] byArray) throws IOException, TSPException {
        try {
            return TimeStampResp.getInstance(byArray);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TSPException("malformed timestamp response: " + illegalArgumentException, illegalArgumentException);
        }
        catch (ClassCastException classCastException) {
            throw new TSPException("malformed timestamp response: " + classCastException, classCastException);
        }
    }

    private static TimeStampResp parseTimeStampResp(InputStream inputStream) throws IOException, TSPException {
        try {
            return TimeStampResp.getInstance(new ASN1InputStream(inputStream).readObject());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TSPException("malformed timestamp response: " + illegalArgumentException, illegalArgumentException);
        }
        catch (ClassCastException classCastException) {
            throw new TSPException("malformed timestamp response: " + classCastException, classCastException);
        }
    }

    public TimeStampResponse(TimeStampResp timeStampResp) throws TSPException, IOException {
        this.resp = timeStampResp;
        ContentInfo contentInfo = timeStampResp.getTimeStampToken();
        this.timeStampToken = contentInfo == null ? null : new TimeStampToken(contentInfo);
    }

    public TimeStampResponse(byte[] byArray) throws TSPException, IOException {
        this(TimeStampResponse.parseTimeStampResp(byArray));
    }

    public TimeStampResponse(InputStream inputStream) throws TSPException, IOException {
        this(TimeStampResponse.parseTimeStampResp(inputStream));
    }

    TimeStampResponse(DLSequence dLSequence) throws TSPException, IOException {
        try {
            this.resp = TimeStampResp.getInstance(dLSequence);
            this.timeStampToken = new TimeStampToken(ContentInfo.getInstance(dLSequence.getObjectAt(1)));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TSPException("malformed timestamp response: " + illegalArgumentException, illegalArgumentException);
        }
        catch (ClassCastException classCastException) {
            throw new TSPException("malformed timestamp response: " + classCastException, classCastException);
        }
    }

    public int getStatus() {
        return this.resp.getStatus().getStatusObject().intValueExact();
    }

    public String getStatusString() {
        if (this.resp.getStatus().getStatusString() == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        PKIFreeText pKIFreeText = this.resp.getStatus().getStatusString();
        for (int i = 0; i != pKIFreeText.size(); ++i) {
            stringBuilder.append(pKIFreeText.getStringAtUTF8(i).getString());
        }
        return stringBuilder.toString();
    }

    public PKIFailureInfo getFailInfo() {
        if (this.resp.getStatus().getFailInfo() != null) {
            return new PKIFailureInfo(this.resp.getStatus().getFailInfo());
        }
        return null;
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public void validate(TimeStampRequest timeStampRequest) throws TSPException {
        TimeStampToken timeStampToken = this.getTimeStampToken();
        if (timeStampToken != null) {
            TimeStampTokenInfo timeStampTokenInfo = timeStampToken.getTimeStampInfo();
            if (timeStampRequest.getNonce() != null && !timeStampRequest.getNonce().equals(timeStampTokenInfo.getNonce())) {
                throw new TSPValidationException("response contains wrong nonce value.");
            }
            if (this.getStatus() != 0 && this.getStatus() != 1) {
                throw new TSPValidationException("time stamp token found in failed request.");
            }
            if (!timeStampTokenInfo.getMessageImprintAlgOID().equals(timeStampRequest.getMessageImprintAlgOID())) {
                throw new TSPValidationException("response for different message imprint algorithm.");
            }
            if (!Arrays.constantTimeAreEqual(timeStampRequest.getMessageImprintDigest(), timeStampTokenInfo.getMessageImprintDigest())) {
                throw new TSPValidationException("response for different message imprint digest.");
            }
            Attribute attribute = timeStampToken.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            Attribute attribute2 = timeStampToken.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
            if (attribute == null && attribute2 == null) {
                throw new TSPValidationException("no signing certificate attribute present.");
            }
            if (attribute == null || attribute2 != null) {
                // empty if block
            }
            if (timeStampRequest.getReqPolicy() != null && !timeStampRequest.getReqPolicy().equals(timeStampTokenInfo.getPolicy())) {
                throw new TSPValidationException("TSA policy wrong for request.");
            }
        } else if (this.getStatus() == 0 || this.getStatus() == 1) {
            throw new TSPValidationException("no time stamp token found and one expected.");
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public byte[] getEncoded(String string) throws IOException {
        ASN1Object aSN1Object = this.resp;
        if ("DL".equals(string)) {
            aSN1Object = this.timeStampToken == null ? new DLSequence(this.resp.getStatus()) : new DLSequence(this.resp.getStatus(), this.timeStampToken.toCMSSignedData().toASN1Structure());
        }
        return aSN1Object.getEncoded(string);
    }
}

