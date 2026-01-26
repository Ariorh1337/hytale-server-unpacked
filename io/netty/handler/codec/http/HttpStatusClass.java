/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public enum HttpStatusClass {
    INFORMATIONAL(100, 200, "Informational"),
    SUCCESS(200, 300, "Success"),
    REDIRECTION(300, 400, "Redirection"),
    CLIENT_ERROR(400, 500, "Client Error"),
    SERVER_ERROR(500, 600, "Server Error"),
    UNKNOWN(0, 0, "Unknown Status"){

        @Override
        public boolean contains(int code) {
            return code < 100 || code >= 600;
        }
    };

    private static final HttpStatusClass[] statusArray;
    private final int min;
    private final int max;
    private final AsciiString defaultReasonPhrase;

    public static HttpStatusClass valueOf(int code) {
        if (UNKNOWN.contains(code)) {
            return UNKNOWN;
        }
        return statusArray[HttpStatusClass.fast_div100(code)];
    }

    private static int fast_div100(int dividend) {
        return (int)((long)dividend * 1374389535L >> 37);
    }

    public static HttpStatusClass valueOf(CharSequence code) {
        if (code != null && code.length() == 3) {
            char c0 = code.charAt(0);
            return HttpStatusClass.isDigit(c0) && HttpStatusClass.isDigit(code.charAt(1)) && HttpStatusClass.isDigit(code.charAt(2)) ? HttpStatusClass.valueOf(HttpStatusClass.digit(c0) * 100) : UNKNOWN;
        }
        return UNKNOWN;
    }

    private static int digit(char c) {
        return c - 48;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private HttpStatusClass(int min, int max, String defaultReasonPhrase) {
        this.min = min;
        this.max = max;
        this.defaultReasonPhrase = AsciiString.cached(defaultReasonPhrase);
    }

    public boolean contains(int code) {
        return code >= this.min && code < this.max;
    }

    AsciiString defaultReasonPhrase() {
        return this.defaultReasonPhrase;
    }

    static {
        statusArray = new HttpStatusClass[6];
        HttpStatusClass.statusArray[1] = INFORMATIONAL;
        HttpStatusClass.statusArray[2] = SUCCESS;
        HttpStatusClass.statusArray[3] = REDIRECTION;
        HttpStatusClass.statusArray[4] = CLIENT_ERROR;
        HttpStatusClass.statusArray[5] = SERVER_ERROR;
    }
}

