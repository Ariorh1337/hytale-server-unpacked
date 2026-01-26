/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ChannelInitializerExtension;
import io.netty.bootstrap.ChannelInitializerExtensions;
import io.netty.bootstrap.FailedChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel>
implements Cloneable {
    private static final boolean CLOSE_ON_SET_OPTION_FAILURE = SystemPropertyUtil.getBoolean("io.netty.bootstrap.closeOnSetOptionFailure", true);
    private static final Map.Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY = new Map.Entry[0];
    private static final Map.Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY = new Map.Entry[0];
    volatile EventLoopGroup group;
    private volatile ChannelFactory<? extends C> channelFactory;
    private volatile SocketAddress localAddress;
    private final Map<ChannelOption<?>, Object> options = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap();
    private volatile ChannelHandler handler;
    private volatile ClassLoader extensionsClassLoader;

    AbstractBootstrap() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    AbstractBootstrap(AbstractBootstrap<B, C> bootstrap) {
        this.group = bootstrap.group;
        this.channelFactory = bootstrap.channelFactory;
        this.handler = bootstrap.handler;
        this.localAddress = bootstrap.localAddress;
        Map<ChannelOption<?>, Object> map = bootstrap.options;
        synchronized (map) {
            this.options.putAll(bootstrap.options);
        }
        this.attrs.putAll(bootstrap.attrs);
        this.extensionsClassLoader = bootstrap.extensionsClassLoader;
    }

    public B group(EventLoopGroup group) {
        ObjectUtil.checkNotNull(group, "group");
        if (this.group != null) {
            throw new IllegalStateException("group set already");
        }
        this.group = group;
        return this.self();
    }

    private B self() {
        return (B)this;
    }

    public B channel(Class<? extends C> channelClass) {
        return this.channelFactory((io.netty.channel.ChannelFactory<? extends C>)new ReflectiveChannelFactory<C>(ObjectUtil.checkNotNull(channelClass, "channelClass")));
    }

    @Deprecated
    public B channelFactory(ChannelFactory<? extends C> channelFactory) {
        ObjectUtil.checkNotNull(channelFactory, "channelFactory");
        if (this.channelFactory != null) {
            throw new IllegalStateException("channelFactory set already");
        }
        this.channelFactory = channelFactory;
        return this.self();
    }

    public B channelFactory(io.netty.channel.ChannelFactory<? extends C> channelFactory) {
        return this.channelFactory((ChannelFactory<? extends C>)channelFactory);
    }

    public B localAddress(SocketAddress localAddress) {
        this.localAddress = localAddress;
        return this.self();
    }

    public B localAddress(int inetPort) {
        return this.localAddress(new InetSocketAddress(inetPort));
    }

    public B localAddress(String inetHost, int inetPort) {
        return this.localAddress(SocketUtils.socketAddress(inetHost, inetPort));
    }

    public B localAddress(InetAddress inetHost, int inetPort) {
        return this.localAddress(new InetSocketAddress(inetHost, inetPort));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> B option(ChannelOption<T> option, T value) {
        ObjectUtil.checkNotNull(option, "option");
        Map<ChannelOption<?>, Object> map = this.options;
        synchronized (map) {
            if (value == null) {
                this.options.remove(option);
            } else {
                this.options.put(option, value);
            }
        }
        return this.self();
    }

    public <T> B attr(AttributeKey<T> key, T value) {
        ObjectUtil.checkNotNull(key, "key");
        if (value == null) {
            this.attrs.remove(key);
        } else {
            this.attrs.put(key, value);
        }
        return this.self();
    }

    public B extensionsClassLoader(ClassLoader classLoader) {
        this.extensionsClassLoader = classLoader;
        return this.self();
    }

    public B validate() {
        if (this.group == null) {
            throw new IllegalStateException("group not set");
        }
        if (this.channelFactory == null) {
            throw new IllegalStateException("channel or channelFactory not set");
        }
        return this.self();
    }

    public abstract B clone();

    public ChannelFuture register() {
        this.validate();
        return this.initAndRegister();
    }

    public ChannelFuture bind() {
        this.validate();
        SocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            throw new IllegalStateException("localAddress not set");
        }
        return this.doBind(localAddress);
    }

    public ChannelFuture bind(int inetPort) {
        return this.bind(new InetSocketAddress(inetPort));
    }

    public ChannelFuture bind(String inetHost, int inetPort) {
        return this.bind(SocketUtils.socketAddress(inetHost, inetPort));
    }

    public ChannelFuture bind(InetAddress inetHost, int inetPort) {
        return this.bind(new InetSocketAddress(inetHost, inetPort));
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        this.validate();
        return this.doBind(ObjectUtil.checkNotNull(localAddress, "localAddress"));
    }

    private ChannelFuture doBind(final SocketAddress localAddress) {
        final ChannelFuture regFuture = this.initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }
        if (regFuture.isDone()) {
            ChannelPromise promise = channel.newPromise();
            AbstractBootstrap.doBind0(regFuture, channel, localAddress, promise);
            return promise;
        }
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        regFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    promise.setFailure(cause);
                } else {
                    promise.registered();
                    AbstractBootstrap.doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }

    final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            channel = (Channel)this.channelFactory.newChannel();
            this.init(channel);
        }
        catch (Throwable t) {
            if (channel != null) {
                channel.unsafe().closeForcibly();
                return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
            }
            return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        ChannelFuture regFuture = this.config().group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }
        return regFuture;
    }

    abstract void init(Channel var1) throws Throwable;

    Collection<ChannelInitializerExtension> getInitializerExtensions() {
        ClassLoader loader = this.extensionsClassLoader;
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        return ChannelInitializerExtensions.getExtensions().extensions(loader);
    }

    private static void doBind0(final ChannelFuture regFuture, final Channel channel, final SocketAddress localAddress, final ChannelPromise promise) {
        channel.eventLoop().execute(new Runnable(){

            @Override
            public void run() {
                if (regFuture.isSuccess()) {
                    channel.bind(localAddress, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    promise.setFailure(regFuture.cause());
                }
            }
        });
    }

    public B handler(ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        return this.self();
    }

    @Deprecated
    public final EventLoopGroup group() {
        return this.group;
    }

    public abstract AbstractBootstrapConfig<B, C> config();

    final Map.Entry<ChannelOption<?>, Object>[] newOptionsArray() {
        return AbstractBootstrap.newOptionsArray(this.options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Map.Entry<ChannelOption<?>, Object>[] newOptionsArray(Map<ChannelOption<?>, Object> options) {
        Map<ChannelOption<?>, Object> map = options;
        synchronized (map) {
            return new LinkedHashMap(options).entrySet().toArray(EMPTY_OPTION_ARRAY);
        }
    }

    final Map.Entry<AttributeKey<?>, Object>[] newAttributesArray() {
        return AbstractBootstrap.newAttributesArray(this.attrs0());
    }

    static Map.Entry<AttributeKey<?>, Object>[] newAttributesArray(Map<AttributeKey<?>, Object> attributes) {
        return attributes.entrySet().toArray(EMPTY_ATTRIBUTE_ARRAY);
    }

    final Map<ChannelOption<?>, Object> options0() {
        return this.options;
    }

    final Map<AttributeKey<?>, Object> attrs0() {
        return this.attrs;
    }

    final SocketAddress localAddress() {
        return this.localAddress;
    }

    final ChannelFactory<? extends C> channelFactory() {
        return this.channelFactory;
    }

    final ChannelHandler handler() {
        return this.handler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final Map<ChannelOption<?>, Object> options() {
        Map<ChannelOption<?>, Object> map = this.options;
        synchronized (map) {
            return AbstractBootstrap.copiedMap(this.options);
        }
    }

    final Map<AttributeKey<?>, Object> attrs() {
        return AbstractBootstrap.copiedMap(this.attrs);
    }

    static <K, V> Map<K, V> copiedMap(Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<K, V>(map));
    }

    static void setAttributes(Channel channel, Map.Entry<AttributeKey<?>, Object>[] attrs) {
        for (Map.Entry<AttributeKey<?>, Object> e : attrs) {
            AttributeKey<?> key = e.getKey();
            channel.attr(key).set(e.getValue());
        }
    }

    static void setChannelOptions(Channel channel, Map.Entry<ChannelOption<?>, Object>[] options, InternalLogger logger) throws Throwable {
        for (Map.Entry<ChannelOption<?>, Object> e : options) {
            AbstractBootstrap.setChannelOption(channel, e.getKey(), e.getValue(), logger);
        }
    }

    private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value, InternalLogger logger) throws Throwable {
        block3: {
            try {
                if (!channel.config().setOption(option, value)) {
                    logger.warn("Unknown channel option '{}' for channel '{}' of type '{}'", option, channel, channel.getClass());
                }
            }
            catch (Throwable t) {
                logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}' of type '{}'", option, value, channel, channel.getClass(), t);
                if (!CLOSE_ON_SET_OPTION_FAILURE) break block3;
                throw t;
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append('(').append(this.config()).append(')');
        return buf.toString();
    }

    static final class PendingRegistrationPromise
    extends DefaultChannelPromise {
        private volatile boolean registered;

        PendingRegistrationPromise(Channel channel) {
            super(channel);
        }

        void registered() {
            this.registered = true;
        }

        @Override
        protected EventExecutor executor() {
            if (this.registered) {
                return super.executor();
            }
            return GlobalEventExecutor.INSTANCE;
        }
    }
}

