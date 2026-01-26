/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class LegacyBitsOfSecurityConstraint
extends ServicesConstraint {
    private final int requiredBitsOfSecurity;
    private final int legacyRequiredBitsOfSecurity;

    public LegacyBitsOfSecurityConstraint(int n) {
        this(n, 0);
    }

    public LegacyBitsOfSecurityConstraint(int n, int n2) {
        super(Collections.EMPTY_SET);
        this.requiredBitsOfSecurity = n;
        this.legacyRequiredBitsOfSecurity = n2;
    }

    public LegacyBitsOfSecurityConstraint(int n, Set<String> set) {
        this(n, 0, set);
    }

    public LegacyBitsOfSecurityConstraint(int n, int n2, Set<String> set) {
        super(set);
        this.requiredBitsOfSecurity = n;
        this.legacyRequiredBitsOfSecurity = n2;
    }

    @Override
    public void check(CryptoServiceProperties cryptoServiceProperties) {
        if (this.isException(cryptoServiceProperties.getServiceName())) {
            return;
        }
        CryptoServicePurpose cryptoServicePurpose = cryptoServiceProperties.getPurpose();
        switch (cryptoServicePurpose) {
            case ANY: 
            case VERIFYING: 
            case DECRYPTION: 
            case VERIFICATION: {
                if (cryptoServiceProperties.bitsOfSecurity() < this.legacyRequiredBitsOfSecurity) {
                    throw new CryptoServiceConstraintsException("service does not provide " + this.legacyRequiredBitsOfSecurity + " bits of security only " + cryptoServiceProperties.bitsOfSecurity());
                }
                if (cryptoServicePurpose != CryptoServicePurpose.ANY && LOG.isLoggable(Level.FINE)) {
                    LOG.fine("usage of legacy cryptography service for algorithm " + cryptoServiceProperties.getServiceName());
                }
                return;
            }
        }
        if (cryptoServiceProperties.bitsOfSecurity() < this.requiredBitsOfSecurity) {
            throw new CryptoServiceConstraintsException("service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + cryptoServiceProperties.bitsOfSecurity());
        }
    }
}

