/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util.filter;

import org.bouncycastle.pkix.util.filter.Filter;

public class SQLFilter
implements Filter {
    @Override
    public String doFilter(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        block11: for (int i = 0; i < stringBuilder.length(); ++i) {
            char c = stringBuilder.charAt(i);
            switch (c) {
                case '\'': {
                    stringBuilder.replace(i, i + 1, "\\'");
                    ++i;
                    continue block11;
                }
                case '\"': {
                    stringBuilder.replace(i, i + 1, "\\\"");
                    ++i;
                    continue block11;
                }
                case '=': {
                    stringBuilder.replace(i, i + 1, "\\=");
                    ++i;
                    continue block11;
                }
                case '-': {
                    stringBuilder.replace(i, i + 1, "\\-");
                    ++i;
                    continue block11;
                }
                case '/': {
                    stringBuilder.replace(i, i + 1, "\\/");
                    ++i;
                    continue block11;
                }
                case '\\': {
                    stringBuilder.replace(i, i + 1, "\\\\");
                    ++i;
                    continue block11;
                }
                case ';': {
                    stringBuilder.replace(i, i + 1, "\\;");
                    ++i;
                    continue block11;
                }
                case '\r': {
                    stringBuilder.replace(i, i + 1, "\\r");
                    ++i;
                    continue block11;
                }
                case '\n': {
                    stringBuilder.replace(i, i + 1, "\\n");
                    ++i;
                    continue block11;
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String doFilterUrl(String string) {
        return this.doFilter(string);
    }
}

