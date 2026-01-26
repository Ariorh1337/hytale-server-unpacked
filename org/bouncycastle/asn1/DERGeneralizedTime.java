/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Strings;

public class DERGeneralizedTime
extends ASN1GeneralizedTime {
    public DERGeneralizedTime(byte[] byArray) {
        super(byArray);
    }

    public DERGeneralizedTime(Date date) {
        super(date);
    }

    public DERGeneralizedTime(String string) {
        super(string);
    }

    private byte[] getDERTime() {
        if (this.contents[this.contents.length - 1] == 90) {
            if (!this.hasMinutes()) {
                byte[] byArray = new byte[this.contents.length + 4];
                System.arraycopy(this.contents, 0, byArray, 0, this.contents.length - 1);
                System.arraycopy(Strings.toByteArray("0000Z"), 0, byArray, this.contents.length - 1, 5);
                return byArray;
            }
            if (!this.hasSeconds()) {
                byte[] byArray = new byte[this.contents.length + 2];
                System.arraycopy(this.contents, 0, byArray, 0, this.contents.length - 1);
                System.arraycopy(Strings.toByteArray("00Z"), 0, byArray, this.contents.length - 1, 3);
                return byArray;
            }
            if (this.hasFractionalSeconds()) {
                int n;
                for (n = this.contents.length - 2; n > 0 && this.contents[n] == 48; --n) {
                }
                if (this.contents[n] == 46) {
                    byte[] byArray = new byte[n + 1];
                    System.arraycopy(this.contents, 0, byArray, 0, n);
                    byArray[n] = 90;
                    return byArray;
                }
                byte[] byArray = new byte[n + 2];
                System.arraycopy(this.contents, 0, byArray, 0, n + 1);
                byArray[n + 1] = 90;
                return byArray;
            }
            return this.contents;
        }
        return this.contents;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.getDERTime().length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 24, this.getDERTime());
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

