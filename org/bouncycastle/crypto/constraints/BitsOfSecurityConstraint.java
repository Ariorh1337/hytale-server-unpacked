/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class BitsOfSecurityConstraint
extends ServicesConstraint {
    private final int requiredBitsOfSecurity;

    public BitsOfSecurityConstraint(int n) {
        super(Collections.EMPTY_SET);
        this.requiredBitsOfSecurity = n;
    }

    public BitsOfSecurityConstraint(int n, Set<String> set) {
        super(set);
        this.requiredBitsOfSecurity = n;
    }

    @Override
    public void check(CryptoServiceProperties cryptoServiceProperties) {
        if (this.isException(cryptoServiceProperties.getServiceName())) {
            return;
        }
        if (cryptoServiceProperties.bitsOfSecurity() < this.requiredBitsOfSecurity) {
            throw new CryptoServiceConstraintsException("service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + cryptoServiceProperties.bitsOfSecurity());
        }
    }
}

