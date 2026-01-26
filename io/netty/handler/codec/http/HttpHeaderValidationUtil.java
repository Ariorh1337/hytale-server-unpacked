/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;

public final class HttpHeaderValidationUtil {
    private HttpHeaderValidationUtil() {
    }

    public static boolean isConnectionHeader(CharSequence name, boolean ignoreTeHeader) {
        int len = name.length();
        switch (len) {
            case 2: {
                return ignoreTeHeader ? false : AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TE);
            }
            case 7: {
                return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.UPGRADE);
            }
            case 10: {
                return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.CONNECTION) || AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.KEEP_ALIVE);
            }
            case 16: {
                return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.PROXY_CONNECTION);
            }
            case 17: {
                return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TRANSFER_ENCODING);
            }
        }
        return false;
    }

    public static boolean isTeNotTrailers(CharSequence name, CharSequence value) {
        if (name.length() == 2) {
            return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TE) && !AsciiString.contentEqualsIgnoreCase(value, HttpHeaderValues.TRAILERS);
        }
        return false;
    }

    public static int validateValidHeaderValue(CharSequence value) {
        int length = value.length();
        if (length == 0) {
            return -1;
        }
        if (value instanceof AsciiString) {
            return HttpHeaderValidationUtil.verifyValidHeaderValueAsciiString((AsciiString)value);
        }
        return HttpHeaderValidationUtil.verifyValidHeaderValueCharSequence(value);
    }

    private static int verifyValidHeaderValueAsciiString(AsciiString value) {
        int start;
        byte[] array = value.array();
        int b = array[start = value.arrayOffset()] & 0xFF;
        if (b < 33 || b == 127) {
            return 0;
        }
        int end = start + value.length();
        for (int i = start + 1; i < end; ++i) {
            b = array[i] & 0xFF;
            if ((b >= 32 || b == 9) && b != 127) continue;
            return i - start;
        }
        return -1;
    }

    private static int verifyValidHeaderValueCharSequence(CharSequence value) {
        char b = value.charAt(0);
        if (b < '!' || b == '\u007f') {
            return 0;
        }
        int length = value.length();
        for (int i = 1; i < length; ++i) {
            b = value.charAt(i);
            if ((b >= ' ' || b == '\t') && b != '\u007f') continue;
            return i;
        }
        return -1;
    }

    public static int validateToken(CharSequence token) {
        return HttpUtil.validateToken(token);
    }
}

