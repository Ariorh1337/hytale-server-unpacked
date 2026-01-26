/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassUtil {
    public static Class loadClass(Class clazz, String string) {
        try {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(string);
            }
            return (Class)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                        return classLoader.loadClass(string);
                    }
                    catch (Exception exception) {
                        return null;
                    }
                }
            });
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }
}

