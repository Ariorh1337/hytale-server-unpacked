/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1UTCTime
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTCTime.class, 23){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1UTCTime.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTCTime getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1UTCTime) {
            return (ASN1UTCTime)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1UTCTime) {
            return (ASN1UTCTime)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1UTCTime)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1UTCTime getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UTCTime)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1UTCTime getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UTCTime)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public ASN1UTCTime(String string) {
        this.contents = Strings.toByteArray(string);
        try {
            this.getDate();
        }
        catch (ParseException parseException) {
            throw new IllegalArgumentException("invalid date string: " + parseException.getMessage());
        }
    }

    public ASN1UTCTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    public ASN1UTCTime(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    ASN1UTCTime(byte[] byArray) {
        if (byArray.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.contents = byArray;
        if (!this.isDigit(0) || !this.isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssz", LocaleUtil.EN_Locale);
        return simpleDateFormat.parse(this.getTime());
    }

    public Date getAdjustedDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat.parse(this.getAdjustedTime());
    }

    public String getTime() {
        String string = Strings.fromByteArray(this.contents);
        if (string.indexOf(45) < 0 && string.indexOf(43) < 0) {
            if (string.length() == 11) {
                return string.substring(0, 10) + "00GMT+00:00";
            }
            return string.substring(0, 12) + "GMT+00:00";
        }
        int n = string.indexOf(45);
        if (n < 0) {
            n = string.indexOf(43);
        }
        String string2 = string;
        if (n == string.length() - 3) {
            string2 = string2 + "00";
        }
        if (n == 10) {
            return string2.substring(0, 10) + "00GMT" + string2.substring(10, 13) + ":" + string2.substring(13, 15);
        }
        return string2.substring(0, 12) + "GMT" + string2.substring(12, 15) + ":" + string2.substring(15, 17);
    }

    public String getAdjustedTime() {
        String string = this.getTime();
        if (string.charAt(0) < '5') {
            return "20" + string;
        }
        return "19" + string;
    }

    private boolean isDigit(int n) {
        return this.contents.length > n && this.contents[n] >= 48 && this.contents[n] <= 57;
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.contents.length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 23, this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1UTCTime)) {
            return false;
        }
        return Arrays.areEqual(this.contents, ((ASN1UTCTime)aSN1Primitive).contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1UTCTime createPrimitive(byte[] byArray) {
        return new ASN1UTCTime(byArray);
    }
}

