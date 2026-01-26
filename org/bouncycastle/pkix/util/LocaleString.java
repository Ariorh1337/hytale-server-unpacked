/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import org.bouncycastle.pkix.util.LocalizedMessage;

public class LocaleString
extends LocalizedMessage {
    public LocaleString(String string, String string2) {
        super(string, string2);
    }

    public LocaleString(String string, String string2, String string3) throws NullPointerException, UnsupportedEncodingException {
        super(string, string2, string3);
    }

    public LocaleString(String string, String string2, String string3, Object[] objectArray) throws NullPointerException, UnsupportedEncodingException {
        super(string, string2, string3, objectArray);
    }

    public String getLocaleString(Locale locale) {
        return this.getEntry(null, locale, null);
    }
}

