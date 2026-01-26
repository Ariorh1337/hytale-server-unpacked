/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class LoggingConstraint
extends ServicesConstraint {
    protected LoggingConstraint(Set<String> set) {
        super(set);
    }

    @Override
    public void check(CryptoServiceProperties cryptoServiceProperties) {
        if (this.isException(cryptoServiceProperties.getServiceName())) {
            return;
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("service " + cryptoServiceProperties.getServiceName() + " referenced [" + cryptoServiceProperties.getServiceName() + ", " + cryptoServiceProperties.bitsOfSecurity() + ", " + (Object)((Object)cryptoServiceProperties.getPurpose()));
        }
    }
}

