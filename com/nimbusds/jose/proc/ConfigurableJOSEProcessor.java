/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.proc.JOSEProcessor;
import com.nimbusds.jose.proc.JOSEProcessorConfiguration;
import com.nimbusds.jose.proc.SecurityContext;

public interface ConfigurableJOSEProcessor<C extends SecurityContext>
extends JOSEProcessor<C>,
JOSEProcessorConfiguration<C> {
}

