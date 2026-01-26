/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.util.DerUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public final class DERMacData {
    private final byte[] macData;

    private DERMacData(byte[] byArray) {
        this.macData = byArray;
    }

    public byte[] getMacData() {
        return Arrays.clone(this.macData);
    }

    public static final class Builder {
        private final Type type;
        private ASN1OctetString idU;
        private ASN1OctetString idV;
        private ASN1OctetString ephemDataU;
        private ASN1OctetString ephemDataV;
        private byte[] text;

        public Builder(Type type, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
            this.type = type;
            this.idU = DerUtil.getOctetString(byArray);
            this.idV = DerUtil.getOctetString(byArray2);
            this.ephemDataU = DerUtil.getOctetString(byArray3);
            this.ephemDataV = DerUtil.getOctetString(byArray4);
        }

        public Builder withText(byte[] byArray) {
            this.text = DerUtil.toByteArray(new DERTaggedObject(false, 0, (ASN1Encodable)DerUtil.getOctetString(byArray)));
            return this;
        }

        public DERMacData build() {
            switch (this.type.ordinal()) {
                case 0: 
                case 2: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.ephemDataU), DerUtil.toByteArray(this.ephemDataV), this.text));
                }
                case 1: 
                case 3: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.ephemDataV), DerUtil.toByteArray(this.ephemDataU), this.text));
                }
            }
            throw new IllegalStateException("Unknown type encountered in build");
        }

        private byte[] concatenate(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6) {
            return Arrays.concatenate(Arrays.concatenate(byArray, byArray2, byArray3), Arrays.concatenate(byArray4, byArray5, byArray6));
        }
    }

    public static enum Type {
        UNILATERALU("KC_1_U"),
        UNILATERALV("KC_1_V"),
        BILATERALU("KC_2_U"),
        BILATERALV("KC_2_V");

        private final String enc;

        private Type(String string2) {
            this.enc = string2;
        }

        public byte[] getHeader() {
            return Strings.toByteArray(this.enc);
        }
    }
}

