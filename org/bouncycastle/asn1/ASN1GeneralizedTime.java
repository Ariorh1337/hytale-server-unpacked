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
import java.util.TimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1GeneralizedTime
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1GeneralizedTime.class, 24){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1GeneralizedTime.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1GeneralizedTime getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1GeneralizedTime) {
            return (ASN1GeneralizedTime)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1GeneralizedTime) {
            return (ASN1GeneralizedTime)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1GeneralizedTime)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1GeneralizedTime getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1GeneralizedTime)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1GeneralizedTime getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1GeneralizedTime)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public ASN1GeneralizedTime(String string) {
        this.contents = Strings.toByteArray(string);
        try {
            this.getDate();
        }
        catch (ParseException parseException) {
            throw new IllegalArgumentException("invalid date string: " + parseException.getMessage());
        }
    }

    public ASN1GeneralizedTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    public ASN1GeneralizedTime(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    ASN1GeneralizedTime(byte[] byArray) {
        if (byArray.length < 4) {
            throw new IllegalArgumentException("GeneralizedTime string too short");
        }
        this.contents = byArray;
        if (!(this.isDigit(0) && this.isDigit(1) && this.isDigit(2) && this.isDigit(3))) {
            throw new IllegalArgumentException("illegal characters in GeneralizedTime string");
        }
    }

    public String getTimeString() {
        return Strings.fromByteArray(this.contents);
    }

    public String getTime() {
        String string = Strings.fromByteArray(this.contents);
        if (string.charAt(string.length() - 1) == 'Z') {
            return string.substring(0, string.length() - 1) + "GMT+00:00";
        }
        int n = string.length() - 6;
        char c = string.charAt(n);
        if ((c == '-' || c == '+') && string.indexOf("GMT") == n - 3) {
            return string;
        }
        n = string.length() - 5;
        c = string.charAt(n);
        if (c == '-' || c == '+') {
            return string.substring(0, n) + "GMT" + string.substring(n, n + 3) + ":" + string.substring(n + 3);
        }
        n = string.length() - 3;
        c = string.charAt(n);
        if (c == '-' || c == '+') {
            return string.substring(0, n) + "GMT" + string.substring(n) + ":00";
        }
        return string + this.calculateGMTOffset(string);
    }

    private String calculateGMTOffset(String string) {
        String string2 = "+";
        TimeZone timeZone = TimeZone.getDefault();
        int n = timeZone.getRawOffset();
        if (n < 0) {
            string2 = "-";
            n = -n;
        }
        int n2 = n / 3600000;
        int n3 = (n - n2 * 60 * 60 * 1000) / 60000;
        try {
            if (timeZone.useDaylightTime()) {
                SimpleDateFormat simpleDateFormat;
                if (this.hasFractionalSeconds()) {
                    string = this.pruneFractionalSeconds(string);
                }
                if (timeZone.inDaylightTime((simpleDateFormat = this.calculateGMTDateFormat()).parse(string + "GMT" + string2 + this.convert(n2) + ":" + this.convert(n3)))) {
                    n2 += string2.equals("+") ? 1 : -1;
                }
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return "GMT" + string2 + this.convert(n2) + ":" + this.convert(n3);
    }

    private SimpleDateFormat calculateGMTDateFormat() {
        SimpleDateFormat simpleDateFormat = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSSz") : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmssz") : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmmz") : new SimpleDateFormat("yyyyMMddHHz")));
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat;
    }

    private String pruneFractionalSeconds(String string) {
        char c;
        int n;
        String string2 = string.substring(14);
        for (n = 1; n < string2.length() && '0' <= (c = string2.charAt(n)) && c <= '9'; ++n) {
        }
        if (n - 1 > 3) {
            string2 = string2.substring(0, 4) + string2.substring(n);
            string = string.substring(0, 14) + string2;
        } else if (n - 1 == 1) {
            string2 = string2.substring(0, n) + "00" + string2.substring(n);
            string = string.substring(0, 14) + string2;
        } else if (n - 1 == 2) {
            string2 = string2.substring(0, n) + "0" + string2.substring(n);
            string = string.substring(0, 14) + string2;
        }
        return string;
    }

    private String convert(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return Integer.toString(n);
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat simpleDateFormat;
        String string;
        String string2 = string = Strings.fromByteArray(this.contents);
        if (string.endsWith("Z")) {
            simpleDateFormat = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'", LocaleUtil.EN_Locale) : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale) : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm'Z'", LocaleUtil.EN_Locale) : new SimpleDateFormat("yyyyMMddHH'Z'", LocaleUtil.EN_Locale)));
            simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        } else if (string.indexOf(45) > 0 || string.indexOf(43) > 0) {
            string2 = this.getTime();
            simpleDateFormat = this.calculateGMTDateFormat();
        } else {
            simpleDateFormat = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSS") : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss") : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm") : new SimpleDateFormat("yyyyMMddHH")));
            simpleDateFormat.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        if (this.hasFractionalSeconds()) {
            string2 = this.pruneFractionalSeconds(string2);
        }
        return simpleDateFormat.parse(string2);
    }

    protected boolean hasFractionalSeconds() {
        for (int i = 0; i != this.contents.length; ++i) {
            if (this.contents[i] != 46 || i != 14) continue;
            return true;
        }
        return false;
    }

    protected boolean hasSeconds() {
        return this.isDigit(12) && this.isDigit(13);
    }

    protected boolean hasMinutes() {
        return this.isDigit(10) && this.isDigit(11);
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
        aSN1OutputStream.writeEncodingDL(bl, 24, this.contents);
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERGeneralizedTime(this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1GeneralizedTime)) {
            return false;
        }
        return Arrays.areEqual(this.contents, ((ASN1GeneralizedTime)aSN1Primitive).contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1GeneralizedTime createPrimitive(byte[] byArray) {
        return new ASN1GeneralizedTime(byArray);
    }
}

