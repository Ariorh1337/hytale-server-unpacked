/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

interface KeyMaterialGenerator {
    public byte[] generateKDFMaterial(AlgorithmIdentifier var1, int var2, byte[] var3);
}

