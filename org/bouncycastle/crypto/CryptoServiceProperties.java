/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.CryptoServicePurpose;

public interface CryptoServiceProperties {
    public int bitsOfSecurity();

    public String getServiceName();

    public CryptoServicePurpose getPurpose();

    public Object getParams();
}

