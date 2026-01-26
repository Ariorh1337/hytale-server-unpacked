/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.est;

import org.bouncycastle.asn1.est.AttrOrOID;

class Utils {
    Utils() {
    }

    static AttrOrOID[] clone(AttrOrOID[] attrOrOIDArray) {
        AttrOrOID[] attrOrOIDArray2 = new AttrOrOID[attrOrOIDArray.length];
        System.arraycopy(attrOrOIDArray, 0, attrOrOIDArray2, 0, attrOrOIDArray.length);
        return attrOrOIDArray2;
    }
}

