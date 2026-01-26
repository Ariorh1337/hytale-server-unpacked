/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;

public class EdDSAParameterSpec
implements AlgorithmParameterSpec {
    public static final String Ed25519 = "Ed25519";
    public static final String Ed448 = "Ed448";
    private final String curveName;

    public EdDSAParameterSpec(String string) {
        if (string.equalsIgnoreCase(Ed25519)) {
            this.curveName = Ed25519;
        } else if (string.equalsIgnoreCase(Ed448)) {
            this.curveName = Ed448;
        } else if (string.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
            this.curveName = Ed25519;
        } else if (string.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
            this.curveName = Ed448;
        } else {
            throw new IllegalArgumentException("unrecognized curve name: " + string);
        }
    }

    public String getCurveName() {
        return this.curveName;
    }
}

