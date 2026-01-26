/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;

public class DefaultServiceProperties
implements CryptoServiceProperties {
    private final String algorithm;
    private final int bitsOfSecurity;
    private final Object params;
    private final CryptoServicePurpose purpose;

    public DefaultServiceProperties(String string, int n) {
        this(string, n, null, CryptoServicePurpose.ANY);
    }

    public DefaultServiceProperties(String string, int n, Object object) {
        this(string, n, object, CryptoServicePurpose.ANY);
    }

    public DefaultServiceProperties(String string, int n, Object object, CryptoServicePurpose cryptoServicePurpose) {
        this.algorithm = string;
        this.bitsOfSecurity = n;
        this.params = object;
        if (object instanceof CryptoServicePurpose) {
            throw new IllegalArgumentException("params should not be CryptoServicePurpose");
        }
        this.purpose = cryptoServicePurpose;
    }

    @Override
    public int bitsOfSecurity() {
        return this.bitsOfSecurity;
    }

    @Override
    public String getServiceName() {
        return this.algorithm;
    }

    @Override
    public CryptoServicePurpose getPurpose() {
        return this.purpose;
    }

    @Override
    public Object getParams() {
        return this.params;
    }
}

