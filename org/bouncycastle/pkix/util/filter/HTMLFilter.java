/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util.filter;

import org.bouncycastle.pkix.util.filter.Filter;

public class HTMLFilter
implements Filter {
    @Override
    public String doFilter(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        block14: for (int i = 0; i < stringBuilder.length(); i += 4) {
            char c = stringBuilder.charAt(i);
            switch (c) {
                case '<': {
                    stringBuilder.replace(i, i + 1, "&#60");
                    continue block14;
                }
                case '>': {
                    stringBuilder.replace(i, i + 1, "&#62");
                    continue block14;
                }
                case '(': {
                    stringBuilder.replace(i, i + 1, "&#40");
                    continue block14;
                }
                case ')': {
                    stringBuilder.replace(i, i + 1, "&#41");
                    continue block14;
                }
                case '#': {
                    stringBuilder.replace(i, i + 1, "&#35");
                    continue block14;
                }
                case '&': {
                    stringBuilder.replace(i, i + 1, "&#38");
                    continue block14;
                }
                case '\"': {
                    stringBuilder.replace(i, i + 1, "&#34");
                    continue block14;
                }
                case '\'': {
                    stringBuilder.replace(i, i + 1, "&#39");
                    continue block14;
                }
                case '%': {
                    stringBuilder.replace(i, i + 1, "&#37");
                    continue block14;
                }
                case ';': {
                    stringBuilder.replace(i, i + 1, "&#59");
                    continue block14;
                }
                case '+': {
                    stringBuilder.replace(i, i + 1, "&#43");
                    continue block14;
                }
                case '-': {
                    stringBuilder.replace(i, i + 1, "&#45");
                    continue block14;
                }
                default: {
                    i -= 3;
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

