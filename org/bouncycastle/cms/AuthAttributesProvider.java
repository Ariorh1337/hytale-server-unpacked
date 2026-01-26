/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Set;

interface AuthAttributesProvider {
    public ASN1Set getAuthAttributes();

    public boolean isAead();
}

