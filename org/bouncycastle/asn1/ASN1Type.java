/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

abstract class ASN1Type {
    final Class javaClass;

    ASN1Type(Class clazz) {
        this.javaClass = clazz;
    }

    final Class getJavaClass() {
        return this.javaClass;
    }

    public final boolean equals(Object object) {
        return this == object;
    }

    public final int hashCode() {
        return super.hashCode();
    }
}

