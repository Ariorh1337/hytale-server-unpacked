/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessorConfiguration;

public interface ConfigurableJWTProcessor<C extends SecurityContext>
extends JWTProcessor<C>,
JWTProcessorConfiguration<C> {
}

