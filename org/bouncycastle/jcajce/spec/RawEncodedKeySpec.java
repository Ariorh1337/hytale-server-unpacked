/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;

public class RawEncodedKeySpec
extends EncodedKeySpec {
    public RawEncodedKeySpec(byte[] byArray) {
        super(byArray);
    }

    @Override
    public String getFormat() {
        return "RAW";
    }
}

