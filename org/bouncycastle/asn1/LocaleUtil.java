/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.util.Longs;

public class LocaleUtil {
    private static final Map localeCache = new HashMap();
    public static Locale EN_Locale = LocaleUtil.forEN();

    private static Locale forEN() {
        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            return Locale.getDefault();
        }
        Locale[] localeArray = Locale.getAvailableLocales();
        for (int i = 0; i != localeArray.length; ++i) {
            if (!"en".equalsIgnoreCase(localeArray[i].getLanguage())) continue;
            return localeArray[i];
        }
        return Locale.getDefault();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Date epochAdjust(Date date) throws ParseException {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return date;
        }
        Map map = localeCache;
        synchronized (map) {
            Long l = (Long)localeCache.get(locale);
            if (l == null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
                long l2 = simpleDateFormat.parse("19700101000000GMT+00:00").getTime();
                l = LocaleUtil.longValueOf(l2);
                localeCache.put(locale, l);
            }
            if (l != 0L) {
                return new Date(date.getTime() - l);
            }
            return date;
        }
    }

    private static Long longValueOf(long l) {
        return Longs.valueOf(l);
    }
}

