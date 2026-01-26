/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

final class ASN1Tag {
    private final int tagClass;
    private final int tagNumber;

    static ASN1Tag create(int n, int n2) {
        return new ASN1Tag(n, n2);
    }

    private ASN1Tag(int n, int n2) {
        this.tagClass = n;
        this.tagNumber = n2;
    }

    int getTagClass() {
        return this.tagClass;
    }

    int getTagNumber() {
        return this.tagNumber;
    }
}

