/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSProcessable;

public interface CMSTypedData
extends CMSProcessable {
    public ASN1ObjectIdentifier getContentType();
}

