/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;

@Deprecated
public interface OpenSslApplicationProtocolNegotiator
extends ApplicationProtocolNegotiator {
    public ApplicationProtocolConfig.Protocol protocol();

    public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior();

    public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior();
}

