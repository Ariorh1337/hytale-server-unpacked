/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x500.style;

public class X500NameTokenizer {
    private final String value;
    private final char separator;
    private int index;

    public X500NameTokenizer(String string) {
        this(string, ',');
    }

    public X500NameTokenizer(String string, char c) {
        if (string == null) {
            throw new NullPointerException();
        }
        if (c == '\"' || c == '\\') {
            throw new IllegalArgumentException("reserved separator character");
        }
        this.value = string;
        this.separator = c;
        this.index = string.length() < 1 ? 0 : -1;
    }

    public boolean hasMoreTokens() {
        return this.index < this.value.length();
    }

    public String nextToken() {
        if (this.index >= this.value.length()) {
            return null;
        }
        boolean bl = false;
        boolean bl2 = false;
        int n = this.index + 1;
        while (++this.index < this.value.length()) {
            char c = this.value.charAt(this.index);
            if (bl2) {
                bl2 = false;
                continue;
            }
            if (c == '\"') {
                bl = !bl;
                continue;
            }
            if (bl) continue;
            if (c == '\\') {
                bl2 = true;
                continue;
            }
            if (c != this.separator) continue;
            return this.value.substring(n, this.index);
        }
        if (bl2 || bl) {
            throw new IllegalArgumentException("badly formatted directory string");
        }
        return this.value.substring(n, this.index);
    }
}

