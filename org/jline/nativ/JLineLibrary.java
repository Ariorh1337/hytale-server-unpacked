/*
 * Decompiled with CFR 0.152.
 */
package org.jline.nativ;

import java.io.FileDescriptor;
import org.jline.nativ.JLineNativeLoader;

public class JLineLibrary {
    public static native FileDescriptor newFileDescriptor(int var0);

    public static native ProcessBuilder.Redirect newRedirectPipe(FileDescriptor var0);

    static {
        JLineNativeLoader.initialize();
    }
}

