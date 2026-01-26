/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import javax.net.ssl.SSLParameters;

final class OpenSslParametersUtil {
    private static final MethodHandle GET_NAMED_GROUPS;
    private static final MethodHandle SET_NAMED_GROUPS;

    private static MethodHandle obtainHandle(MethodHandles.Lookup lookup, String methodName, MethodType type) {
        return AccessController.doPrivileged(() -> {
            try {
                return lookup.findVirtual(SSLParameters.class, methodName, type);
            }
            catch (IllegalAccessException | NoSuchMethodException | SecurityException | UnsupportedOperationException e) {
                return null;
            }
        });
    }

    static String[] getNamesGroups(SSLParameters parameters) {
        if (GET_NAMED_GROUPS == null) {
            return null;
        }
        try {
            return GET_NAMED_GROUPS.invoke(parameters);
        }
        catch (Throwable t) {
            return null;
        }
    }

    static void setNamesGroups(SSLParameters parameters, String[] names) {
        if (SET_NAMED_GROUPS == null) {
            return;
        }
        try {
            SET_NAMED_GROUPS.invoke(parameters, names);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private OpenSslParametersUtil() {
    }

    static {
        MethodHandle getNamedGroups = null;
        MethodHandle setNamedGroups = null;
        if (PlatformDependent.javaVersion() >= 20) {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            getNamedGroups = OpenSslParametersUtil.obtainHandle(lookup, "getNamedGroups", MethodType.methodType(String[].class));
            setNamedGroups = OpenSslParametersUtil.obtainHandle(lookup, "setNamedGroups", MethodType.methodType(Void.TYPE, String[].class));
        }
        GET_NAMED_GROUPS = getNamedGroups;
        SET_NAMED_GROUPS = setNamedGroups;
    }
}

