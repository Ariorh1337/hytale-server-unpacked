/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkAlpnSslUtils;
import io.netty.handler.ssl.SslUtils;
import io.netty.handler.ssl.util.BouncyCastleUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

final class BouncyCastleAlpnSslUtils {
    private static final InternalLogger logger;
    private static final Method SET_APPLICATION_PROTOCOLS;
    private static final Method GET_APPLICATION_PROTOCOL;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
    private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Class<?> BC_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;
    private static final boolean SUPPORTED;

    private BouncyCastleAlpnSslUtils() {
    }

    static String getApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_APPLICATION_PROTOCOL.invoke((Object)sslEngine, new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
        String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);
        try {
            SSLParameters bcSslParameters = engine.getSSLParameters();
            SET_APPLICATION_PROTOCOLS.invoke((Object)bcSslParameters, new Object[]{protocolArray});
            engine.setSSLParameters(bcSslParameters);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        if (PlatformDependent.javaVersion() >= 9) {
            JdkAlpnSslUtils.setApplicationProtocols(engine, supportedProtocols);
        }
    }

    static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke((Object)sslEngine, new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void setHandshakeApplicationProtocolSelector(SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
        try {
            Object selectorProxyInstance = Proxy.newProxyInstance(BouncyCastleAlpnSslUtils.class.getClassLoader(), new Class[]{BC_APPLICATION_PROTOCOL_SELECTOR}, new InvocationHandler(){

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("select")) {
                        try {
                            return selector.apply((SSLEngine)args[0], (List)args[1]);
                        }
                        catch (ClassCastException e) {
                            throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", e);
                        }
                    }
                    throw new UnsupportedOperationException(String.format("Method '%s' not supported.", method.getName()));
                }
            });
            SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke((Object)engine, selectorProxyInstance);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
        try {
            Object selector = GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke((Object)engine, new Object[0]);
            return (sslEngine, strings) -> {
                try {
                    return (String)BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(selector, sslEngine, strings);
                }
                catch (Exception e) {
                    throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", e);
                }
            };
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static boolean isAlpnSupported() {
        return SUPPORTED;
    }

    static {
        boolean supported;
        Method getHandshakeApplicationProtocolSelector;
        Method setHandshakeApplicationProtocolSelector;
        Method bcApplicationProtocolSelectorSelect;
        Class<?> bcApplicationProtocolSelector;
        Method getHandshakeApplicationProtocol;
        Method getApplicationProtocol;
        Method setApplicationProtocols;
        logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
        try {
            Class<?> testBCApplicationProtocolSelector;
            if (!BouncyCastleUtil.isBcTlsAvailable()) {
                throw new IllegalStateException(BouncyCastleUtil.unavailabilityCauseBcTls());
            }
            SSLContext context = SslUtils.getSSLContext(BouncyCastleUtil.getBcProviderJsse(), new SecureRandom());
            SSLEngine engine = context.createSSLEngine();
            Class<?> engineClass = engine.getClass();
            final Class<? extends SSLEngine> bcEngineClass = BouncyCastleUtil.getBcSSLEngineClass();
            if (bcEngineClass == null || !bcEngineClass.isAssignableFrom(engineClass)) {
                throw new IllegalStateException("Unexpected engine class: " + engineClass);
            }
            SSLParameters bcSslParameters = engine.getSSLParameters();
            final Class<?> bCSslParametersClass = bcSslParameters.getClass();
            setApplicationProtocols = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return bCSslParametersClass.getMethod("setApplicationProtocols", String[].class);
                }
            });
            setApplicationProtocols.invoke((Object)bcSslParameters, new Object[]{EmptyArrays.EMPTY_STRINGS});
            getApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return bcEngineClass.getMethod("getApplicationProtocol", new Class[0]);
                }
            });
            getApplicationProtocol.invoke((Object)engine, new Object[0]);
            getHandshakeApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return bcEngineClass.getMethod("getHandshakeApplicationProtocol", new Class[0]);
                }
            });
            getHandshakeApplicationProtocol.invoke((Object)engine, new Object[0]);
            bcApplicationProtocolSelector = testBCApplicationProtocolSelector = Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector", true, engineClass.getClassLoader());
            bcApplicationProtocolSelectorSelect = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return testBCApplicationProtocolSelector.getMethod("select", Object.class, List.class);
                }
            });
            setHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return bcEngineClass.getMethod("setBCHandshakeApplicationProtocolSelector", testBCApplicationProtocolSelector);
                }
            });
            getHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return bcEngineClass.getMethod("getBCHandshakeApplicationProtocolSelector", new Class[0]);
                }
            });
            getHandshakeApplicationProtocolSelector.invoke((Object)engine, new Object[0]);
            supported = true;
        }
        catch (Throwable t) {
            logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", t);
            setApplicationProtocols = null;
            getApplicationProtocol = null;
            getHandshakeApplicationProtocol = null;
            setHandshakeApplicationProtocolSelector = null;
            getHandshakeApplicationProtocolSelector = null;
            bcApplicationProtocolSelectorSelect = null;
            bcApplicationProtocolSelector = null;
            supported = false;
        }
        SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
        GET_APPLICATION_PROTOCOL = getApplicationProtocol;
        GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
        SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
        GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
        BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = bcApplicationProtocolSelectorSelect;
        BC_APPLICATION_PROTOCOL_SELECTOR = bcApplicationProtocolSelector;
        SUPPORTED = supported;
    }
}

