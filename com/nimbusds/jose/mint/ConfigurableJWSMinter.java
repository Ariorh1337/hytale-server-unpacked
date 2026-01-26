/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.mint;

import com.nimbusds.jose.mint.JWSMinter;
import com.nimbusds.jose.mint.JWSMinterConfiguration;
import com.nimbusds.jose.proc.SecurityContext;

public interface ConfigurableJWSMinter<C extends SecurityContext>
extends JWSMinter<C>,
JWSMinterConfiguration<C> {
}

