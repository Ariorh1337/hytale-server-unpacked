/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.MetaData;

public class TimeStampedDataParser {
    private ASN1Integer version;
    private ASN1IA5String dataUri;
    private MetaData metaData;
    private ASN1OctetStringParser content;
    private Evidence temporalEvidence;
    private ASN1SequenceParser parser;

    private TimeStampedDataParser(ASN1SequenceParser aSN1SequenceParser) throws IOException {
        this.parser = aSN1SequenceParser;
        this.version = ASN1Integer.getInstance(aSN1SequenceParser.readObject());
        ASN1Encodable aSN1Encodable = aSN1SequenceParser.readObject();
        if (aSN1Encodable instanceof ASN1IA5String) {
            this.dataUri = ASN1IA5String.getInstance(aSN1Encodable);
            aSN1Encodable = aSN1SequenceParser.readObject();
        }
        if (aSN1Encodable instanceof MetaData || aSN1Encodable instanceof ASN1SequenceParser) {
            this.metaData = MetaData.getInstance(aSN1Encodable.toASN1Primitive());
            aSN1Encodable = aSN1SequenceParser.readObject();
        }
        if (aSN1Encodable instanceof ASN1OctetStringParser) {
            this.content = (ASN1OctetStringParser)aSN1Encodable;
        }
    }

    public static TimeStampedDataParser getInstance(Object object) throws IOException {
        if (object instanceof ASN1Sequence) {
            return new TimeStampedDataParser(((ASN1Sequence)object).parser());
        }
        if (object instanceof ASN1SequenceParser) {
            return new TimeStampedDataParser((ASN1SequenceParser)object);
        }
        return null;
    }

    public int getVersion() {
        return this.version.getValue().intValue();
    }

    public DERIA5String getDataUri() {
        return null == this.dataUri || this.dataUri instanceof DERIA5String ? (DERIA5String)this.dataUri : new DERIA5String(this.dataUri.getString(), false);
    }

    public ASN1IA5String getDataUriIA5() {
        return this.dataUri;
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public ASN1OctetStringParser getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() throws IOException {
        if (this.temporalEvidence == null) {
            this.temporalEvidence = Evidence.getInstance(this.parser.readObject().toASN1Primitive());
        }
        return this.temporalEvidence;
    }
}

