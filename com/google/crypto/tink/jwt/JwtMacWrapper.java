/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.jwt.JwtInvalidException;
import com.google.crypto.tink.jwt.JwtMac;
import com.google.crypto.tink.jwt.JwtValidator;
import com.google.crypto.tink.jwt.RawJwt;
import com.google.crypto.tink.jwt.VerifiedJwt;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

class JwtMacWrapper
implements PrimitiveWrapper<JwtMac, JwtMac> {
    private static final JwtMacWrapper WRAPPER = new JwtMacWrapper();

    private static void validate(KeysetHandleInterface keysetHandle) throws GeneralSecurityException {
        if (keysetHandle.getPrimary() == null) {
            throw new GeneralSecurityException("Primitive set has no primary.");
        }
    }

    JwtMacWrapper() {
    }

    @Override
    public JwtMac wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<JwtMac> factory) throws GeneralSecurityException {
        MonitoringClient.Logger verifyLogger;
        MonitoringClient.Logger computeLogger;
        JwtMacWrapper.validate(keysetHandle);
        ArrayList<JwtMacWithId> allMacs = new ArrayList<JwtMacWithId>(keysetHandle.size());
        for (int i = 0; i < keysetHandle.size(); ++i) {
            KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            JwtMac jwtMac = factory.create(entry);
            allMacs.add(new JwtMacWithId(jwtMac, entry.getId()));
        }
        if (!annotations.isEmpty()) {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            computeLogger = client.createLogger(keysetHandle, annotations, "jwtmac", "compute");
            verifyLogger = client.createLogger(keysetHandle, annotations, "jwtmac", "verify");
        } else {
            computeLogger = MonitoringUtil.DO_NOTHING_LOGGER;
            verifyLogger = MonitoringUtil.DO_NOTHING_LOGGER;
        }
        JwtMac primaryMac = factory.create(keysetHandle.getPrimary());
        return new WrappedJwtMac(new JwtMacWithId(primaryMac, keysetHandle.getPrimary().getId()), allMacs, computeLogger, verifyLogger);
    }

    @Override
    public Class<JwtMac> getPrimitiveClass() {
        return JwtMac.class;
    }

    @Override
    public Class<JwtMac> getInputPrimitiveClass() {
        return JwtMac.class;
    }

    public static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
    }

    private static class JwtMacWithId {
        final JwtMac jwtMac;
        final int id;

        JwtMacWithId(JwtMac jwtMac, int id) {
            this.jwtMac = jwtMac;
            this.id = id;
        }
    }

    @Immutable
    private static class WrappedJwtMac
    implements JwtMac {
        private final JwtMacWithId primary;
        private final List<JwtMacWithId> allMacs;
        private final MonitoringClient.Logger computeLogger;
        private final MonitoringClient.Logger verifyLogger;

        private WrappedJwtMac(JwtMacWithId primary, List<JwtMacWithId> allMacs, MonitoringClient.Logger computeLogger, MonitoringClient.Logger verifyLogger) {
            this.primary = primary;
            this.allMacs = allMacs;
            this.computeLogger = computeLogger;
            this.verifyLogger = verifyLogger;
        }

        @Override
        public String computeMacAndEncode(RawJwt token) throws GeneralSecurityException {
            try {
                String result = this.primary.jwtMac.computeMacAndEncode(token);
                this.computeLogger.log(this.primary.id, 1L);
                return result;
            }
            catch (GeneralSecurityException e) {
                this.computeLogger.logFailure();
                throw e;
            }
        }

        @Override
        public VerifiedJwt verifyMacAndDecode(String compact, JwtValidator validator) throws GeneralSecurityException {
            GeneralSecurityException interestingException = null;
            for (JwtMacWithId macAndId : this.allMacs) {
                try {
                    VerifiedJwt result = macAndId.jwtMac.verifyMacAndDecode(compact, validator);
                    this.verifyLogger.log(macAndId.id, 1L);
                    return result;
                }
                catch (GeneralSecurityException e) {
                    if (!(e instanceof JwtInvalidException)) continue;
                    interestingException = e;
                }
            }
            this.verifyLogger.logFailure();
            if (interestingException != null) {
                throw interestingException;
            }
            throw new GeneralSecurityException("invalid MAC");
        }
    }
}

