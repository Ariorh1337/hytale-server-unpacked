/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.nio;

import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;

final class SelectorProviderUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelectorProviderUtil.class);

    static Method findOpenMethod(String methodName) {
        if (PlatformDependent.javaVersion() >= 15) {
            try {
                return SelectorProvider.class.getMethod(methodName, ProtocolFamily.class);
            }
            catch (Throwable e) {
                logger.debug("SelectorProvider.{}(ProtocolFamily) not available, will use default", (Object)methodName, (Object)e);
            }
        }
        return null;
    }

    private static <C extends Channel> C newChannel(Method method, SelectorProvider provider, Object family) throws IOException {
        if (family != null && method != null) {
            try {
                Channel channel = (Channel)method.invoke((Object)provider, family);
                return (C)channel;
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    static <C extends Channel> C newChannel(Method method, SelectorProvider provider, SocketProtocolFamily family) throws IOException {
        if (family != null) {
            return SelectorProviderUtil.newChannel(method, provider, family.toJdkFamily());
        }
        return null;
    }

    static <C extends Channel> C newDomainSocketChannel(Method method, SelectorProvider provider) throws IOException {
        return SelectorProviderUtil.newChannel(method, provider, StandardProtocolFamily.valueOf("UNIX"));
    }

    private SelectorProviderUtil() {
    }
}

