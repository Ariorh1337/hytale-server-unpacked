/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.spec.AlgorithmParameterSpec;

public class SpecUtil {
    private static Class[] NO_PARAMS = new Class[0];
    private static Object[] NO_ARGS = new Object[0];

    public static String getNameFrom(AlgorithmParameterSpec algorithmParameterSpec) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    Method method = algorithmParameterSpec.getClass().getMethod("getName", NO_PARAMS);
                    return method.invoke((Object)algorithmParameterSpec, NO_ARGS);
                }
                catch (Exception exception) {
                    return null;
                }
            }
        });
    }

    public static byte[] getContextFrom(AlgorithmParameterSpec algorithmParameterSpec) {
        return (byte[])AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    Method method = algorithmParameterSpec.getClass().getMethod("getContext", NO_PARAMS);
                    return method.invoke((Object)algorithmParameterSpec, NO_ARGS);
                }
                catch (Exception exception) {
                    return null;
                }
            }
        });
    }
}

