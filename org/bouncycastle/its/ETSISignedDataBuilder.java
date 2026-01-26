/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.its.ETSISignedData;
import org.bouncycastle.its.ITSAlgorithmUtils;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload;
import org.bouncycastle.oer.its.ieee1609dot2.SignerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;

public class ETSISignedDataBuilder {
    private static final Element def = IEEE1609dot2.ToBeSignedData.build();
    private final HeaderInfo headerInfo;
    private Ieee1609Dot2Data data;
    private HashedData extDataHash;

    private ETSISignedDataBuilder(Psid psid) {
        this(HeaderInfo.builder().setPsid(psid).setGenerationTime(Time64.now()).createHeaderInfo());
    }

    private ETSISignedDataBuilder(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public static ETSISignedDataBuilder builder(Psid psid) {
        return new ETSISignedDataBuilder(psid);
    }

    public static ETSISignedDataBuilder builder(HeaderInfo headerInfo) {
        return new ETSISignedDataBuilder(headerInfo);
    }

    public ETSISignedDataBuilder setData(Ieee1609Dot2Content ieee1609Dot2Content) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(ieee1609Dot2Content).createIeee1609Dot2Data();
        return this;
    }

    public ETSISignedDataBuilder setUnsecuredData(byte[] byArray) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(Ieee1609Dot2Content.unsecuredData(new Opaque(byArray))).createEtsiTs103097Data();
        return this;
    }

    public ETSISignedDataBuilder setExtDataHash(HashedData hashedData) {
        this.extDataHash = hashedData;
        return this;
    }

    private ToBeSignedData getToBeSignedData() {
        SignedDataPayload signedDataPayload = new SignedDataPayload(this.data, this.extDataHash);
        return ToBeSignedData.builder().setPayload(signedDataPayload).setHeaderInfo(this.headerInfo).createToBeSignedData();
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        Signature signature = ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.self()).setSignature(signature).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner, List<ITSCertificate> list) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        ArrayList<Certificate> arrayList = new ArrayList<Certificate>();
        for (ITSCertificate iTSCertificate : list) {
            arrayList.add(Certificate.getInstance(iTSCertificate.toASN1Structure()));
        }
        Signature signature = ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.certificate(new SequenceOfCertificate(arrayList))).setSignature(signature).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner, HashedId8 hashedId8) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        Signature signature = ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.digest(hashedId8)).setSignature(signature).createSignedData());
    }

    private static void write(OutputStream outputStream, byte[] byArray) {
        try {
            outputStream.write(byArray);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}

