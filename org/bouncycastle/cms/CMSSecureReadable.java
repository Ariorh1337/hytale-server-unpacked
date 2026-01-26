/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.cms.CMSException;

interface CMSSecureReadable {
    public ASN1ObjectIdentifier getContentType();

    public InputStream getInputStream() throws IOException, CMSException;

    public ASN1Set getAuthAttrSet();

    public void setAuthAttrSet(ASN1Set var1);

    public boolean hasAdditionalData();
}

